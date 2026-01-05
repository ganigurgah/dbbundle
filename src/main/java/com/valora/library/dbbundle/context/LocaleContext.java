package com.valora.library.dbbundle.context;

import java.util.Locale;

public class LocaleContext {
    private static final ThreadLocal<Locale> currentLocale = ThreadLocal.withInitial(Locale::getDefault);

    public static void setLocale(Locale locale) { currentLocale.set(locale); }
    public static Locale getLocale() { return currentLocale.get(); }
    public static void clear() { currentLocale.remove(); }
}