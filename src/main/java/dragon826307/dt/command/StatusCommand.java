package dragon826307.dt.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.sun.management.OperatingSystemMXBean;
import dragon826307.dt.config.ConfigProjectManager;
import dragon826307.dt.config.ConfigProjects;
import dragon826307.dt.server.ServerConfigProjectManager;
import dragon826307.dt.util.ServerTranslationUtil;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OperatingSystem;

import java.io.File;
import java.lang.management.ManagementFactory;

public class StatusCommand implements ServerCommandCallback{
    private static final String FALLBACK_JVM = null;
    private static final SystemInfo SYSTEM_INFO = new SystemInfo();
    private static final CentralProcessor PROCESSOR = SYSTEM_INFO.getHardware().getProcessor();
    private static final GlobalMemory GLOBAL_MEMORY = SYSTEM_INFO.getHardware().getMemory();
    private static final OperatingSystem OPERATING = SYSTEM_INFO.getOperatingSystem();
    private static final OperatingSystemMXBean OPERATING_SYSTEM_MX_BEAN = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private static final Runtime RUNTIME = Runtime.getRuntime();
    private static final Object[] JVM_INFO_LIST = new Object[]{System.getProperty("java.vm.name"),null,null,null,null,null,null};
    private static final Object[] SYSTEM_INFO_LIST = new Object[]{OPERATING.toString(),null,null,null,null,PROCESSOR.getProcessorIdentifier().getName(),null,null,null,new File(".").getTotalSpace()/1073741824};
    @Override
    public LiteralArgumentBuilder<ServerCommandSource> addCommandBranch(LiteralArgumentBuilder<ServerCommandSource> thisCommandBranch) {
        return thisCommandBranch.then(CommandManager.literal("java").executes(context -> {
            updateJVMInfo();
            context.getSource().sendFeedback(() -> Text.translatableWithFallback("status_command.jvm_info", ServerTranslationUtil.get("status_command.jvm_info"),JVM_INFO_LIST).withColor(0x55FFFF),false);
            return 1;
        })).then(CommandManager.literal("system").executes(context -> {
            updateSystemInfo();
            context.getSource().sendFeedback(() -> Text.translatableWithFallback("status_command.system_info", ServerTranslationUtil.get("status_command.system_info"),SYSTEM_INFO_LIST).withColor(0x55FFFF),false);
            return 1;
        })).then(CommandManager.literal("minecraft")).requires(source -> source.hasPermissionLevel(ServerConfigProjectManager.getConfig(ConfigProjects.Server.STATUS_COMMAND_PERMISSION).asInt()));
    }
    @Override
    public String setBranchName() {
        return "status";
    }
    private static void updateJVMInfo(){
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime()/1000;
        JVM_INFO_LIST[1] = uptime/86400;
        JVM_INFO_LIST[2] = (uptime/3600)%24;
        JVM_INFO_LIST[3] = (uptime/60)%60;
        JVM_INFO_LIST[4] = uptime%60;
        JVM_INFO_LIST[5] = String.format("%.1f", OPERATING_SYSTEM_MX_BEAN.getProcessCpuLoad()*100);
        JVM_INFO_LIST[6] = (RUNTIME.maxMemory()-RUNTIME.freeMemory())/1048576;
    }
    private static void updateSystemInfo(){
        long uptime = OPERATING.getSystemUptime();
        SYSTEM_INFO_LIST[1] = uptime/86400;
        SYSTEM_INFO_LIST[2] = (uptime/3600)%24;
        SYSTEM_INFO_LIST[3] = (uptime/60)%60;
        SYSTEM_INFO_LIST[4] = uptime%60;
        SYSTEM_INFO_LIST[6] = String.format("%.1f", OPERATING_SYSTEM_MX_BEAN.getCpuLoad()*100);
        SYSTEM_INFO_LIST[7] = GLOBAL_MEMORY.toString();
        SYSTEM_INFO_LIST[8] = new File(".").getFreeSpace()/1073741824;
    }
}
