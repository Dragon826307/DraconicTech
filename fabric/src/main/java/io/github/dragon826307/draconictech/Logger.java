package io.github.dragon826307.draconictech;

import org.slf4j.Marker;

public final class Logger implements org.slf4j.Logger {
    private final org.slf4j.Logger delegate;
    private final String prefix;

    public Logger(org.slf4j.Logger delegate, String prefix) {
        this.delegate = delegate;
        this.prefix = "[" + prefix + "] ";
    }

    private String p(String msg) {
        return prefix + msg;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return delegate.isTraceEnabled();
    }

    @Override
    public void trace(String msg) {
        if (isTraceEnabled()) delegate.trace(p(msg));
    }

    @Override
    public void trace(String format, Object arg) {
        if (isTraceEnabled()) delegate.trace(p(format), arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (isTraceEnabled()) delegate.trace(p(format), arg1, arg2);
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (isTraceEnabled()) delegate.trace(p(format), arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (isTraceEnabled()) delegate.trace(p(msg), t);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return delegate.isTraceEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String msg) {
        if (isTraceEnabled(marker)) delegate.trace(marker, p(msg));
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        if (isTraceEnabled(marker)) delegate.trace(marker, p(format), arg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        if (isTraceEnabled(marker)) delegate.trace(marker, p(format), arg1, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
        if (isTraceEnabled(marker)) delegate.trace(marker, p(format), argArray);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        if (isTraceEnabled(marker)) delegate.trace(marker, p(msg), t);
    }

    @Override
    public boolean isDebugEnabled() {
        return delegate.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        if (isDebugEnabled()) delegate.debug(p(msg));
    }

    @Override
    public void debug(String format, Object arg) {
        if (isDebugEnabled()) delegate.debug(p(format), arg);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (isDebugEnabled()) delegate.debug(p(format), arg1, arg2);
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (isDebugEnabled()) delegate.debug(p(format), arguments);
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (isDebugEnabled()) delegate.debug(p(msg), t);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return delegate.isDebugEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String msg) {
        if (isDebugEnabled(marker)) delegate.debug(marker, p(msg));
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        if (isDebugEnabled(marker)) delegate.debug(marker, p(format), arg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        if (isDebugEnabled(marker)) delegate.debug(marker, p(format), arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object... argArray) {
        if (isDebugEnabled(marker)) delegate.debug(marker, p(format), argArray);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        if (isDebugEnabled(marker)) delegate.debug(marker, p(msg), t);
    }

    @Override
    public boolean isInfoEnabled() {
        return delegate.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        if (isInfoEnabled()) delegate.info(p(msg));
    }

    @Override
    public void info(String format, Object arg) {
        if (isInfoEnabled()) delegate.info(p(format), arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (isInfoEnabled()) delegate.info(p(format), arg1, arg2);
    }

    @Override
    public void info(String format, Object... arguments) {
        if (isInfoEnabled()) delegate.info(p(format), arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
        if (isInfoEnabled()) delegate.info(p(msg), t);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return delegate.isInfoEnabled(marker);
    }

    @Override
    public void info(Marker marker, String msg) {
        if (isInfoEnabled(marker)) delegate.info(marker, p(msg));
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        if (isInfoEnabled(marker)) delegate.info(marker, p(format), arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        if (isInfoEnabled(marker)) delegate.info(marker, p(format), arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object... argArray) {
        if (isInfoEnabled(marker)) delegate.info(marker, p(format), argArray);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        if (isInfoEnabled(marker)) delegate.info(marker, p(msg), t);
    }

    @Override
    public boolean isWarnEnabled() {
        return delegate.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        if (isWarnEnabled()) delegate.warn(p(msg));
    }

    @Override
    public void warn(String format, Object arg) {
        if (isWarnEnabled()) delegate.warn(p(format), arg);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (isWarnEnabled()) delegate.warn(p(format), arg1, arg2);
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (isWarnEnabled()) delegate.warn(p(format), arguments);
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (isWarnEnabled()) delegate.warn(p(msg), t);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return delegate.isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String msg) {
        if (isWarnEnabled(marker)) delegate.warn(marker, p(msg));
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        if (isWarnEnabled(marker)) delegate.warn(marker, p(format), arg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        if (isWarnEnabled(marker)) delegate.warn(marker, p(format), arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object... argArray) {
        if (isWarnEnabled(marker)) delegate.warn(marker, p(format), argArray);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        if (isWarnEnabled(marker)) delegate.warn(marker, p(msg), t);
    }

    @Override
    public boolean isErrorEnabled() {
        return delegate.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        if (isErrorEnabled()) delegate.error(p(msg));
    }

    @Override
    public void error(String format, Object arg) {
        if (isErrorEnabled()) delegate.error(p(format), arg);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (isErrorEnabled()) delegate.error(p(format), arg1, arg2);
    }

    @Override
    public void error(String format, Object... arguments) {
        if (isErrorEnabled()) delegate.error(p(format), arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        if (isErrorEnabled()) delegate.error(p(msg), t);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return delegate.isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String msg) {
        if (isErrorEnabled(marker)) delegate.error(marker, p(msg));
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        if (isErrorEnabled(marker)) delegate.error(marker, p(format), arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        if (isErrorEnabled(marker)) delegate.error(marker, p(format), arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object... argArray) {
        if (isErrorEnabled(marker)) delegate.error(marker, p(format), argArray);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        if (isErrorEnabled(marker)) delegate.error(marker, p(msg), t);
    }
}
