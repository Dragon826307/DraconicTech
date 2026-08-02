package dragon826307.dt.features.microtick;

public final class MicroTickingFlags {
    public static final int COMMAND_FUNCTION = 1;//指令函数
    public static final int WORLD_BORDER = 2;//世界边界
    public static final int WEATHER = 4;//天气状态
    public static final int AMBIENT_DARKNESS = 8;//氛围阴影
    public static final int PENDING_BLOCK = 16;//方块计划刻
    public static final int PENDING_FLUID = 32;//流体计划刻
    public static final int RAID = 64;//袭击
    public static final int CHUNK = 128;//区块
    public static final int BLOCK_EVENT = 256;//方块事件
    public static final int DRAGON_FIGHT = 512;//龙战
    public static final int ENTITIES = 1024;//实体事件
    public static final int BLOCK_ENTITIES = 2048;//方块实体
    public static final int ENTITY_LOAD_CHUNK = 4096;
    public static final int ENTITY_UNLOAD_CHUNK = 8192;
    public static final int PLAYER = 16384;//玩家
}
