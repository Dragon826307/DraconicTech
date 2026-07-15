package dragon826307.dt.client.util.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Vec3d;

public class Render3DBoxTask extends RenderTask<Render3DBoxTask> {
    private final float x1, y1, z1, x2, y2, z2;
    protected Render3DBoxTask(float x1, float y1, float z1, float x2, float y2, float z2) {
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }
    protected void render(WorldRenderContext worldRenderContext) {
        matrixStack = worldRenderContext.matrices();
        Vec3d cameraPos = worldRenderContext.worldState().cameraRenderState.pos;
        matrixStack.push();
        matrixStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        matrix4f = matrixStack.peek().getPositionMatrix();
        if (bufferBuilder == null) {
            BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

            bufferBuilder
            .vertex(matrix4f,x1,y1,z2).color(ARGB_color)
            .vertex(matrix4f,x2,y1,z2).color(ARGB_color)
            .vertex(matrix4f,x2,y2,z2).color(ARGB_color)
            .vertex(matrix4f,x1,y2,z2).color(ARGB_color)

            .vertex(matrix4f,x1,y2,z1).color(ARGB_color)
            .vertex(matrix4f,x2,y2,z1).color(ARGB_color)
            .vertex(matrix4f,x2,y1,z1).color(ARGB_color)
            .vertex(matrix4f,x1,y1,z1).color(ARGB_color)

            .vertex(matrix4f,x1,y2,z2).color(ARGB_color)
            .vertex(matrix4f,x2,y2,z2).color(ARGB_color)
            .vertex(matrix4f,x2,y2,z1).color(ARGB_color)
            .vertex(matrix4f,x1,y2,z1).color(ARGB_color)

            .vertex(matrix4f,x1,y1,z1).color(ARGB_color)
            .vertex(matrix4f,x2,y1,z1).color(ARGB_color)
            .vertex(matrix4f,x2,y1,z2).color(ARGB_color)
            .vertex(matrix4f,x1,y1,z2).color(ARGB_color)

            .vertex(matrix4f,x2,y1,z1).color(ARGB_color)
            .vertex(matrix4f,x2,y2,z1).color(ARGB_color)
            .vertex(matrix4f,x2,y2,z2).color(ARGB_color)
            .vertex(matrix4f,x2,y1,z2).color(ARGB_color)

            .vertex(matrix4f,x1,y1,z2).color(ARGB_color)
            .vertex(matrix4f,x1,y2,z2).color(ARGB_color)
            .vertex(matrix4f,x1,y2,z1).color(ARGB_color)
            .vertex(matrix4f,x1,y1,z1).color(ARGB_color);

            builtBuffer = bufferBuilder.end();
        }
        matrixStack.pop();
    }

    @Override
    protected RenderPipeline getPipeline() {
        return RenderTaskPipelines.RENDER_THROUGH_WALLS_QUADS;
    }
}
