package dragon826307.dt.client.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import dragon826307.dt.DraconicTech;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.MappableRingBuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.util.math.Vec3d;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.util.*;
import java.util.List;

public class RenderManager {
    public static final long ID_ONE_SHOT = 0L;

    private static final Map<Long, RenderTask<?>> PERSISTENT_TASKS = new HashMap<>();
    private static final List<RenderTask<?>> ONE_SHOT_TASKS = new ArrayList<>();

    private static final BufferAllocator ALLOCATOR = new BufferAllocator(RenderLayer.CUTOUT_BUFFER_SIZE);
    private static final Vector4f COLOR_MODULATOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final Vector3f MODEL_OFFSET = new Vector3f();
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();

    private static float rainbowColor = 0f;
    private static float rainbowSpeed = 0.005f;
    private static MappableRingBuffer vertexBuffer;
    private static <T extends RenderTask<T>> T submitTask(T task) {
        if (task.getId() == ID_ONE_SHOT) {
            ONE_SHOT_TASKS.add(task);
        }else {
            PERSISTENT_TASKS.put(task.getId(), task);
        }
        return task;
    }
    public static void removeTask(long id) {
        if (id != ID_ONE_SHOT) PERSISTENT_TASKS.remove(id);
    }
    public static void RenderAll(@NonNull WorldRenderContext context) {
        if (PERSISTENT_TASKS.isEmpty() && ONE_SHOT_TASKS.isEmpty()) return;
        rainbowColor += rainbowSpeed;
        int currentRainbowRGB = Color.HSBtoRGB(rainbowColor, 1f, 1f) & 0x00FFFFFF;
        Map<RenderPipeline, List<RenderTask<?>>> pipelineGroup = new HashMap<>();
        PERSISTENT_TASKS.values().removeIf(renderTask -> {
            if (renderTask.isExpired()) return true;
            if (renderTask.isVisible()) {
                pipelineGroup.computeIfAbsent(renderTask.getPipeline(), k -> new ArrayList<>()).add(renderTask);
            }
            return false;
        });
        synchronized (ONE_SHOT_TASKS) {
            for (RenderTask<?> task : ONE_SHOT_TASKS) {
                if (!task.isExpired() && task.isVisible()) {
                    pipelineGroup.computeIfAbsent(task.getPipeline(), k -> new ArrayList<>()).add(task);
                }
            }
            ONE_SHOT_TASKS.clear();
        }
        if (pipelineGroup.isEmpty()) return;
        Vec3d cameraPos = context.worldState().cameraRenderState.pos;
        Matrix4f identityMatrix = new Matrix4f();
        pipelineGroup.forEach((pipeline, tasks) -> {
            VertexFormat.DrawMode drawMode = pipeline.getVertexFormatMode();
            VertexFormat vertexFormat = pipeline.getVertexFormat();
            BufferBuilder bufferBuilder = Tessellator.getInstance().begin(drawMode, vertexFormat);
            for (RenderTask<?> task : tasks) {
                int argb = task.argbColor;
                if (task.isRainbow()) {
                    argb = (task.argbColor & 0xFF000000) | currentRainbowRGB;
                }
                task.buildVertices(bufferBuilder, identityMatrix, cameraPos, argb);
            }
            BuiltBuffer builtBuffer = bufferBuilder.endNullable();
            if (builtBuffer != null) {
                drawBatch(MinecraftClient.getInstance(), pipeline, builtBuffer);
            }
        });
    }
    private static void drawBatch(MinecraftClient client, RenderPipeline pipeline, BuiltBuffer builtBuffer){
        BuiltBuffer.DrawParameters drawParameters = builtBuffer.getDrawParameters();
        VertexFormat vertexFormat = drawParameters.format();
        GpuBuffer vertices = upload(drawParameters, vertexFormat, builtBuffer);
        GpuBuffer indices;
        VertexFormat.IndexType indexType;
        if (pipeline.getVertexFormatMode() == VertexFormat.DrawMode.QUADS){
            builtBuffer.sortQuads(ALLOCATOR, RenderSystem.getProjectionType().getVertexSorter());
            indices = pipeline.getVertexFormat().uploadImmediateIndexBuffer(builtBuffer.getSortedBuffer());
            indexType = builtBuffer.getDrawParameters().indexType();
        }else {
            RenderSystem.ShapeIndexBuffer shapeIndexBuffer = RenderSystem.getSequentialBuffer(pipeline.getVertexFormatMode());
            indices = shapeIndexBuffer.getIndexBuffer(drawParameters.indexCount());
            indexType = shapeIndexBuffer.getIndexType();
        }
        GpuBufferSlice dynamicTransforms = RenderSystem.getDynamicUniforms().write(RenderSystem.getModelViewMatrix(),COLOR_MODULATOR,MODEL_OFFSET,TEXTURE_MATRIX,1);
        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(()-> DraconicTech.MOD_ID+" batched render pipeline",client.getFramebuffer().getColorAttachmentView(), OptionalInt.empty(),client.getFramebuffer().getDepthAttachmentView(), OptionalDouble.empty())) {
            renderPass.setPipeline(pipeline);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms",dynamicTransforms);
            renderPass.setVertexBuffer(0,vertices);
            renderPass.setIndexBuffer(indices,indexType);
            renderPass.drawIndexed(0,0,drawParameters.indexCount(),1);
        }
        builtBuffer.close();
        if (vertexBuffer != null) vertexBuffer.rotate();
    }
    private static GpuBuffer upload(BuiltBuffer.DrawParameters drawParameters, VertexFormat format, BuiltBuffer builtBuffer) {
        int vertexBufferSize = drawParameters.vertexCount() * format.getVertexSize();
        if (vertexBuffer == null || vertexBuffer.size() < vertexBufferSize) {
            if (vertexBuffer != null) {
                vertexBuffer.close();
            }
            vertexBuffer = new MappableRingBuffer(() -> DraconicTech.MOD_ID + "render pipeline rendering", GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_MAP_WRITE, vertexBufferSize);
        }
        CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
        try (GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(vertexBuffer.getBlocking().slice(0, builtBuffer.getBuffer().remaining()), false, true)) {
            MemoryUtil.memCopy(builtBuffer.getBuffer(), mappedView.data());
        }
        return vertexBuffer.getBlocking();
    }
    public static void close() {
        ALLOCATOR.close();
        if (vertexBuffer != null) {
            vertexBuffer.close();
            vertexBuffer = null;
        }
        PERSISTENT_TASKS.clear();
        ONE_SHOT_TASKS.clear();
    }
    public static Render3DBoxTask Render3DBoxTask(long id, float x1, float y1, float z1, float x2, float y2, float z2) {
        return submitTask(new Render3DBoxTask(id, x1, y1, z1, x2, y2, z2));
    }
    public static float getRainbowSpeed() {
        return rainbowSpeed;
    }
    public static void setRainbowSpeed(float rainbowSpeed) {
        RenderManager.rainbowSpeed = rainbowSpeed;
    }
}