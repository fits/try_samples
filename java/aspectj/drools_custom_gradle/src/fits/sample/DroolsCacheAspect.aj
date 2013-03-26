package fits.sample;

import java.util.*;

import org.drools.base.*;
import org.drools.base.ClassFieldAccessorCache.CacheEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

privileged aspect DroolsCacheAspect {

	private static Logger log = LoggerFactory.getLogger(DroolsCacheAspect.class);
	// 既存の cacheByClassLoader の代わりにグローバルキャッシュを導入
	private Map<ClassLoader, CacheEntry> globalCache = new java.util.concurrent.ConcurrentHashMap<ClassLoader, CacheEntry>();

	// ClassFieldAccessorCache に新しいメソッドを追加
	private Map<ClassLoader, CacheEntry> ClassFieldAccessorCache.getCacheByClassLoader() {
		return this.cacheByClassLoader;
	}

	before(Class cls, ClassFieldAccessorCache cfac): execution(public * ClassFieldAccessorCache.getCacheEntry(..)) && args(cls) && this(cfac) {

		ClassLoader cl = (cls.getClassLoader() != null)? cls.getClassLoader(): cfac.classLoader;

		Map<ClassLoader, CacheEntry> map = cfac.getCacheByClassLoader();

		log.info("*** class=" + cls + ", cache=" + map.get(cl) + ", this=" + cfac + ", classloader=" + cl);
	}

	after(ClassFieldAccessorCache cfac): execution(ClassFieldAccessorCache.new(..)) && this(cfac) {
		// cacheByClassLoader を上書き
		cfac.cacheByClassLoader = globalCache;
	}
}
