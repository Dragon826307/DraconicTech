package io.github.dragon826307.draconictech.client.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import io.github.dragon826307.draconictech.DraconicTech;
import io.github.dragon826307.draconictech.util.AutoInitialize;
import io.github.dragon826307.draconictech.util.InitializePhase;
import net.fabricmc.loader.api.FabricLoader;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.api.v0.IrisApi;
import net.irisshaders.iris.api.v0.IrisProgram;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

public abstract class RenderTask<T extends RenderTask<T>> {
    private static boolean irisInitialized;
    
    private final long id;
    private boolean isVisible = true;
    private boolean isRainbow = false;
    private boolean isRemoved = false;
    private long expireTime = 1000;
    protected int argbColor = 0xFFFFFFFF;
    public RenderTask(long id) {
        this.id = id;
    }
    public long getId() { return id; }
    @SuppressWarnings("unchecked")
    public T setColor(int argbColor) {
        this.argbColor = argbColor;
        return (T) this;
    }
    @SuppressWarnings("unchecked")
    public T setRainbow(boolean rainbow) {
        this.isRainbow = rainbow;
        return (T) this;
    }
    @SuppressWarnings("unchecked")
    public T setLifetimeMs(long lifetimeMs) {
        this.expireTime = System.currentTimeMillis() + lifetimeMs;
        return (T) this;
    }
    @SuppressWarnings("unchecked")
    public T setVisible(boolean visible) {
        this.isVisible = visible;
        return (T) this;
    }
    public void close() { this.isRemoved = true; }
    public boolean isVisible() { return isVisible; }
    public boolean isRainbow() { return isRainbow; }
    public boolean isExpired() { return isRemoved || System.currentTimeMillis() > expireTime; }
    public abstract void buildVertices(BufferBuilder bufferBuilder, Matrix4f positionMatrix, Vec3d cameraPos, int finalColor);
    public abstract RenderPipeline getPipeline();
    @AutoInitialize(phase = InitializePhase.ON_CLIENT_STARTED)
    private static void init() {
        if (!irisInitialized && FabricLoader.getInstance().isModLoaded(Iris.MODID)) {
            try {
                if (IrisApi.getInstance().isShaderPackInUse()) {
                    //TODO : 不支持photon光影
                    IrisApi.getInstance().assignPipeline(RenderTaskPipelines.RENDER_THROUGH_WALLS_QUADS, IrisProgram.BASIC);
                    IrisApi.getInstance().assignPipeline(RenderTaskPipelines.RENDER_THROUGH_WALLS_LINES,IrisProgram.BASIC);
                    irisInitialized = true;
                    DraconicTech.LOGGER.info("assign render pipeline to Iris");
                }
            } catch (Exception e) {
                irisInitialized = true;
                DraconicTech.LOGGER.error("Iris mod is loaded but API class NOT found",e);
            }
        }
    }
    protected enum RenderTaskPipelines {;
        static final RenderPipeline RENDER_THROUGH_WALLS_QUADS = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.POSITION_COLOR_SNIPPET).withLocation(Identifier.of(DraconicTech.MOD_ID,"pipeline/through_walls_quads")).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).build());
        static final RenderPipeline RENDER_THROUGH_WALLS_LINES = RenderPipelines.register(RenderPipeline.builder(RenderPipelines.RENDERTYPE_LINES_SNIPPET).withLocation(Identifier.of(DraconicTech.MOD_ID,"pipeline/through_walls_lines")).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).build());
    }
}