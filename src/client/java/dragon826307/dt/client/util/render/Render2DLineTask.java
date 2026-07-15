package dragon826307.dt.client.util.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import org.joml.Matrix4f;

public class Render2DLineTask extends RenderTask<Render2DLineTask> {
    private float x1, y1, x2, y2;
    public Render2DLineTask setX1(float x1) {
        this.x1 = x1;
        return this;
    }
    public Render2DLineTask setY1(float y1) {
        this.y1 = y1;
        return this;
    }
    public Render2DLineTask setX2(float x2) {
        this.x2 = x2;
        return this;
    }
    public Render2DLineTask setY2(float y2) {
        this.y2 = y2;
        return this;
    }
    protected Render2DLineTask(float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
    @Override
    protected void render(WorldRenderContext worldRenderContext) {
        matrixStack = worldRenderContext.matrices();
        matrixStack.push();
//        bufferBuilder = new BufferBuilder(allocator, RENDER_THROUGH_WALLS_QUADS.getVertexFormatMode(), RENDER_THROUGH_WALLS_QUADS.getVertexFormat());
        matrix4f = matrixStack.peek().getPositionMatrix();
//        bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(new Matrix4f(),x1, y1,0).color(ARGB_color);
        bufferBuilder.vertex(new Matrix4f(),x2, y2,0).color(ARGB_color);
        matrixStack.pop();
    }

    @Override
    protected RenderPipeline getPipeline() {
        return null;
    }
}
