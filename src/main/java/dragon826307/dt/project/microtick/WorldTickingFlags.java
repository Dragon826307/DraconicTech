package dragon826307.dt.project.microtick;

public enum WorldTickingFlags{
    COMMAND_FUNCTION,//指令函数
    WORLD_BORDER,//世界边界
    WEATHER,//天气状态
    AMBIENT_DARKNESS,//氛围阴影
    PENDING_BLOCK,//方块计划刻
    PENDING_FLUID,//流体计划刻
    RAID,//袭击
    CHUNK,//区块
    BLOCK_EVENT,//方块事件
    DRAGON_FIGHT,//龙战
    ENTITIES,//实体事件
    BLOCK_ENTITIES,//方块实体
    ENTITY_LOAD_CHUNK,
    ENTITY_UNLOAD_CHUNK,
    PLAYER,//玩家
}
