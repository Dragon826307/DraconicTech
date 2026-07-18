package dragon826307.dt.project.microtick;

public enum WorldTickingFlags{
    COMMAND_FUNCTION(1),//指令函数
    WORLD_BORDER(1),//世界边界
    WEATHER(1),//天气状态
    AMBIENT_DARKNESS(1),//氛围阴影
    PENDING_BLOCK(1),//方块计划刻
    PENDING_FLUID(1),//流体计划刻
    RAID(1),//袭击
    CHUNK(1),//区块
    BLOCK_EVENT(1),//方块事件
    DRAGON_FIGHT(1),//龙战
    ENTITIES(1),//实体事件
    BLOCK_ENTITIES(1),//方块实体
    ENTITY_LOAD_CHUNK(1),
    ENTITY_UNLOAD_CHUNK(1),
    PLAYER(1);//玩家
    public final int frozenLevel;
    WorldTickingFlags(int frozenLevel) {
        this.frozenLevel = frozenLevel;
    }
}
