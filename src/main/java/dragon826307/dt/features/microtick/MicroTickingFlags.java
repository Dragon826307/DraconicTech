package dragon826307.dt.features.microtick;

public final class MicroTickingFlags {
    public static final int COMMAND_FUNCTION = 8;//指令函数
    public static final int WORLD_BORDER = 16;//世界边界
    public static final int WEATHER = 32;//天气状态
    public static final int AMBIENT_DARKNESS = 64;//氛围阴影
    public static final int PENDING_BLOCK = 128;//方块计划刻
    public static final int PENDING_FLUID = 256;//流体计划刻
    public static final int RAID = 512;//袭击
    public static final int CHUNK = 1024;//区块
    public static final int BLOCK_EVENT = 2048;//方块事件
    public static final int DRAGON_FIGHT = 4096;//龙战
    public static final int ENTITIES = 8192;//实体事件
    public static final int BLOCK_ENTITIES = 16384;//方块实体
    public static final int ENTITY_LOAD_CHUNK = 32768;
    public static final int ENTITY_UNLOAD_CHUNK = 65536;
    public static final int PLAYER = 131072;//玩家
}
