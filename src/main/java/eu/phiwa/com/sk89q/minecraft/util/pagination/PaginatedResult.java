/*
 * Decompiled with CFR 0_132.
 */
package eu.phiwa.com.sk89q.minecraft.util.pagination;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.WrappedCommandSender;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class PaginatedResult<T> {
    protected final int resultsPerPage;

    public PaginatedResult() {
        this(6);
    }

    public PaginatedResult(int resultsPerPage) {
        assert (resultsPerPage > 0);
        this.resultsPerPage = resultsPerPage;
    }

    public void display(WrappedCommandSender sender, Collection<? extends T> results, int page) throws CommandException {
        this.display(sender, (List<? extends T>)new ArrayList<T>(results), page);
    }

    public void display(WrappedCommandSender sender, List<? extends T> results, int page) throws CommandException {
        if (results.size() == 0) {
            throw new CommandException("No results match!");
        }
        int maxPages = results.size() / this.resultsPerPage + 1;
        if (results.size() % this.resultsPerPage == 0) {
            --maxPages;
        }
        if (page <= 0 || page > maxPages) {
            throw new CommandException("Unknown page selected! " + maxPages + " total pages.");
        }
        sender.sendMessage(this.formatHeader(page, maxPages));
        for (int i = this.resultsPerPage * (page - 1); i < this.resultsPerPage * page && i < results.size(); ++i) {
            sender.sendMessage(this.format(results.get(i), i));
        }
    }

    public abstract String formatHeader(int var1, int var2);

    public abstract String format(T var1, int var2);
}

