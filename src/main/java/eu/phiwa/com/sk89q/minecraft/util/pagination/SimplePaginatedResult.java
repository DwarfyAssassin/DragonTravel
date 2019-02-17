/*
 * Decompiled with CFR 0_132.
 */
package eu.phiwa.com.sk89q.minecraft.util.pagination;

import com.sk89q.minecraft.util.commands.ChatColor;
import com.sk89q.minecraft.util.pagination.PaginatedResult;

public abstract class SimplePaginatedResult<T>
extends PaginatedResult<T> {
    protected final String header;

    public SimplePaginatedResult(String header) {
        this.header = header;
    }

    public SimplePaginatedResult(String header, int resultsPerPage) {
        super(resultsPerPage);
        this.header = header;
    }

    @Override
    public String formatHeader(int page, int maxPages) {
        return (Object)((Object)ChatColor.YELLOW) + this.header + " (page " + page + "/" + maxPages + ")";
    }
}

