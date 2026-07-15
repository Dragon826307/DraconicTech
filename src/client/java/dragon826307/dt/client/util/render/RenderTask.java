package dragon826307.dt.client.util.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import dragon826307.dt.DraconicTech;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.loader.api.FabricLoader;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.api.v0.IrisProgram;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public abstract class RenderTask<T extends RenderTask<T>> {
    private static boolean IrisInitialized = false;
    protected static MatrixStack matrixStack;
    protected static Matrix4f matrix4f;
    protected BufferBuilder bufferBuilder;
    protected BuiltBuffer builtBuffer;
    private boolean isVisible = true;
    private boolean isRainbow = false;
    protected boolean isRemove = false;
    private long liveTime = System.currentTimeMillis() + 1145;
    protected int ARGB_color = 0xFFFFFFFF;
    @SuppressWarnings("unchecked")
    public T setColor(int ARGB_color) {
        this.ARGB_color = ARGB_color;
        return (T) this;
    }
    @SuppressWarnings("unchecked")
    public T setRainbow(boolean rainbow) {
        isRainbow = rainbow;
        return (T) this;
    }
    @SuppressWarnings("unchecked")
    public T setLifetime_millisSecond(long lifetime) {
        this.liveTime = System.currentTimeMillis() + lifetime;
        return (T) this;
    }
    @SuppressWarnings("unchecked")
    public T setVisible(boolean visible) {
        isVisible = visible;
        return (T) this;
    }
    public void remove() {
        isRemove = true;
    }
    public boolean isVisible() {
        return isVisible;
    }
    public boolean isRainbow() {
        return isRainbow;
    }
    public boolean isRemove() {
        return isRemove || System.currentTimeMillis() > liveTime;
    }
    protected abstract void render(WorldRenderContext worldRenderContext);
    protected abstract RenderPipeline getPipeline();
    public static void init() {
            if (!IrisInitialized && FabricLoader.getInstance().isModLoaded(Iris.MODID)) {
                try {
                    if (IrisApi.getInstance().isShaderPackInUse()) {
                        //TODO : 不支持photon光影
                        IrisApi.getInstance().assignPipeline(RenderTaskPipelines.RENDER_THROUGH_WALLS_QUADS,IrisProgram.BASIC);
                        IrisApi.getInstance().assignPipeline(RenderTaskPipelines.RENDER_THROUGH_WALLS_LINES,IrisProgram.BASIC);
                        IrisInitialized = true;
                        DraconicTech.LOGGER.info("assign render pipeline to Iris");
                    }
                } catch (Exception e) {
                    IrisInitialized = true;
                    DraconicTech.LOGGER.error("Iris mod is loaded but API class NOT found",e);
                }
            }
    }
    protected enum RenderTaskPipelines {;
        static final RenderPipeline RENDER_THROUGH_WALLS_QUADS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET).withLocation(Identifier.of(DraconicTech.MOD_ID,"pipeline/through_walls_quads")).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).build());
        static final RenderPipeline RENDER_THROUGH_WALLS_LINES = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET).withLocation(Identifier.of(DraconicTech.MOD_ID,"pipeline/through_walls_lines")).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).build());
    }
}