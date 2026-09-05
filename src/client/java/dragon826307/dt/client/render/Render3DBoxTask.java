package dragon826307.dt.client.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public class Render3DBoxTask extends RenderTask<Render3DBoxTask> {
    private final float x1, y1, z1, x2, y2, z2;
    protected Render3DBoxTask(long id,float x1, float y1, float z1, float x2, float y2, float z2) {
        super(id);
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }
    public void buildVertices(BufferBuilder builder, Matrix4f matrix, Vec3d camera, int color) {
        float rx1 = (float) (x1 - camera.x), ry1 = (float) (y1 - camera.y), rz1 = (float) (z1 - camera.z);
        float rx2 = (float) (x2 - camera.x), ry2 = (float) (y2 - camera.y), rz2 = (float) (z2 - camera.z);

        builder.vertex(matrix, rx1, ry1, rz2).color(color);
        builder.vertex(matrix, rx2, ry1, rz2).color(color);
        builder.vertex(matrix, rx2, ry2, rz2).color(color);
        builder.vertex(matrix, rx1, ry2, rz2).color(color);

        builder.vertex(matrix, rx1, ry2, rz1).color(color);
        builder.vertex(matrix, rx2, ry2, rz1).color(color);
        builder.vertex(matrix, rx2, ry1, rz1).color(color);
        builder.vertex(matrix, rx1, ry1, rz1).color(color);

        builder.vertex(matrix, rx1, ry2, rz2).color(color);
        builder.vertex(matrix, rx2, ry2, rz2).color(color);
        builder.vertex(matrix, rx2, ry2, rz1).color(color);
        builder.vertex(matrix, rx1, ry2, rz1).color(color);

        builder.vertex(matrix, rx1, ry1, rz1).color(color);
        builder.vertex(matrix, rx2, ry1, rz1).color(color);
        builder.vertex(matrix, rx2, ry1, rz2).color(color);
        builder.vertex(matrix, rx1, ry1, rz2).color(color);

        builder.vertex(matrix, rx2, ry1, rz1).color(color);
        builder.vertex(matrix, rx2, ry2, rz1).color(color);
        builder.vertex(matrix, rx2, ry2, rz2).color(color);
        builder.vertex(matrix, rx2, ry1, rz2).color(color);

        builder.vertex(matrix, rx1, ry1, rz2).color(color);
        builder.vertex(matrix, rx1, ry2, rz2).color(color);
        builder.vertex(matrix, rx1, ry2, rz1).color(color);
        builder.vertex(matrix, rx1, ry1, rz1).color(color);

    }

    @Override
    public RenderPipeline getPipeline() {
        return RenderTaskPipelines.RENDER_THROUGH_WALLS_QUADS;
    }
}
