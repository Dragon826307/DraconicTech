package dragon826307.dt.client.util.render;

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
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.BufferAllocator;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class RenderManager {
    private static final BufferAllocator ALLOCATOR = new BufferAllocator(RenderLayer.CUTOUT_BUFFER_SIZE);
    private static final Vector4f COLOR_MODULATOR = new Vector4f(1f, 1f, 1f, 1f);
    private static final Vector3f MODEL_OFFSET = new Vector3f();
    private static final Matrix4f TEXTURE_MATRIX = new Matrix4f();
    private static final List<RenderTask<?>> renderQueue = new ArrayList<>();
    private static final List<RenderTask<?>> submitBuffer = new ArrayList<>();
    private static float rainbowColor = 0f;
    private static float rainbowSpeed = 0.005f;
    private static MappableRingBuffer vertexBuffer;
    private static <T extends RenderTask<T>> T submitTask(T task) {
        synchronized (submitBuffer) {
            submitBuffer.add(task);
        }
        return task;
    }
    public static void RenderAll(WorldRenderContext context) {
        synchronized (submitBuffer) {
            if (!submitBuffer.isEmpty()) {
                renderQueue.addAll(submitBuffer);
                submitBuffer.clear();
            }
        }
        if (renderQueue.isEmpty()) return;
        rainbowColor += rainbowSpeed;
        renderQueue.removeIf(task -> {
            if (task.isRemove()) return true;
            if (task.isVisible()){
                if (task.isRainbow()) task.setColor((task.ARGB_color & 0xFF000000) | (Color.HSBtoRGB(rainbowColor,1,1) & 0x00FFFFFF));
                try {
                    task.render(context);
                    RenderManager.render(MinecraftClient.getInstance(), task.getPipeline(), task);
                }catch (Exception e) {
                    DraconicTech.LOGGER.error("Render ERROR",e);
                    task.remove();
                    return true;
                }
            }
            return false;
        });
    }
    private static void render(MinecraftClient client, @SuppressWarnings("SameParameterValue") RenderPipeline pipeline, RenderTask<?> task) {
        BuiltBuffer builtBuffer = task.builtBuffer;
        BuiltBuffer.DrawParameters drawParameters = builtBuffer.getDrawParameters();
        VertexFormat format = drawParameters.format();
        GpuBuffer vertices = upload(drawParameters, format, builtBuffer);
        draw(client, pipeline, builtBuffer, drawParameters, vertices, format);
        vertexBuffer.rotate();
    }
    private static void draw(MinecraftClient client, RenderPipeline pipeline, BuiltBuffer builtBuffer, BuiltBuffer.DrawParameters drawParameters, GpuBuffer vertices, VertexFormat format){
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
        try (RenderPass renderPass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(()-> DraconicTech.MOD_ID+"render pipeline",client.getFramebuffer().getColorAttachmentView(), OptionalInt.empty(),client.getFramebuffer().getDepthAttachmentView(), OptionalDouble.empty())) {
            renderPass.setPipeline(pipeline);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.setUniform("DynamicTransforms",dynamicTransforms);
            renderPass.setVertexBuffer(0,vertices);
            renderPass.setIndexBuffer(indices,indexType);
            //noinspection ConstantValue
            renderPass.drawIndexed(0/ format.getVertexSize(),0,drawParameters.indexCount(),1);
        }
        builtBuffer.close();
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
    }
    public static Render2DLineTask Render2DLineTask(float x1, float y1, float x2, float y2) {
        return submitTask(new Render2DLineTask(x1, y1, x2, y2));
    }
    public static Render3DBoxTask Render3DBoxTask(float x1, float y1, float z1, float x2, float y2, float z2) {
        return submitTask(new Render3DBoxTask(x1, y1, z1, x2, y2, z2));
    }
    public static float getRainbowSpeed() {
        return rainbowSpeed;
    }
    public static void setRainbowSpeed(float rainbowSpeed) {
        RenderManager.rainbowSpeed = rainbowSpeed;
    }
}