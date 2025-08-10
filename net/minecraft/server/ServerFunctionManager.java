/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  com.mojang.logging.LogUtils
 */
package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.commands.CommandResultCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.FunctionInstantiationException;
import net.minecraft.commands.execution.ExecutionContext;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.commands.functions.InstantiatedFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionLibrary;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class ServerFunctionManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation TICK_FUNCTION_TAG = ResourceLocation.withDefaultNamespace("tick");
    private static final ResourceLocation LOAD_FUNCTION_TAG = ResourceLocation.withDefaultNamespace("load");
    private final MinecraftServer server;
    private List<CommandFunction<CommandSourceStack>> ticking = ImmutableList.of();
    private boolean postReload;
    private ServerFunctionLibrary library;

    public ServerFunctionManager(MinecraftServer $$0, ServerFunctionLibrary $$1) {
        this.server = $$0;
        this.library = $$1;
        this.postReload($$1);
    }

    public CommandDispatcher<CommandSourceStack> getDispatcher() {
        return this.server.getCommands().getDispatcher();
    }

    public void tick() {
        if (!this.server.tickRateManager().runsNormally()) {
            return;
        }
        if (this.postReload) {
            this.postReload = false;
            List<CommandFunction<CommandSourceStack>> $$0 = this.library.getTag(LOAD_FUNCTION_TAG);
            this.executeTagFunctions($$0, LOAD_FUNCTION_TAG);
        }
        this.executeTagFunctions(this.ticking, TICK_FUNCTION_TAG);
    }

    private void executeTagFunctions(Collection<CommandFunction<CommandSourceStack>> $$0, ResourceLocation $$1) {
        Profiler.get().push($$1::toString);
        for (CommandFunction<CommandSourceStack> $$2 : $$0) {
            this.execute($$2, this.getGameLoopSender());
        }
        Profiler.get().pop();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void execute(CommandFunction<CommandSourceStack> $$0, CommandSourceStack $$1) {
        ProfilerFiller $$22 = Profiler.get();
        $$22.push(() -> "function " + String.valueOf($$0.id()));
        try {
            InstantiatedFunction<CommandSourceStack> $$3 = $$0.instantiate(null, this.getDispatcher());
            Commands.executeCommandInContext($$1, $$2 -> ExecutionContext.queueInitialFunctionCall($$2, $$3, $$1, CommandResultCallback.EMPTY));
        } catch (FunctionInstantiationException $$3) {
        } catch (Exception $$4) {
            LOGGER.warn("Failed to execute function {}", (Object)$$0.id(), (Object)$$4);
        } finally {
            $$22.pop();
        }
    }

    public void replaceLibrary(ServerFunctionLibrary $$0) {
        this.library = $$0;
        this.postReload($$0);
    }

    private void postReload(ServerFunctionLibrary $$0) {
        this.ticking = List.copyOf($$0.getTag(TICK_FUNCTION_TAG));
        this.postReload = true;
    }

    public CommandSourceStack getGameLoopSender() {
        return this.server.createCommandSourceStack().withPermission(2).withSuppressedOutput();
    }

    public Optional<CommandFunction<CommandSourceStack>> get(ResourceLocation $$0) {
        return this.library.getFunction($$0);
    }

    public List<CommandFunction<CommandSourceStack>> getTag(ResourceLocation $$0) {
        return this.library.getTag($$0);
    }

    public Iterable<ResourceLocation> getFunctionNames() {
        return this.library.getFunctions().keySet();
    }

    public Iterable<ResourceLocation> getTagNames() {
        return this.library.getAvailableTags();
    }
}

