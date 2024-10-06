package nl.enjarai.doabarrelroll.config;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public @interface MigrationValue {
    class SerializationStrategy implements ExclusionStrategy {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getAnnotation(MigrationValue.class) != null;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }
}
