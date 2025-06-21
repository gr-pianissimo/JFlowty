package pianissimo.jflowty.functions.conversions;

import java.util.*;
import java.util.function.*;

@FunctionalInterface
public interface SuperSupplier<Output> extends SuperFunction<Void, Output>, Supplier<Output> {
	@Override
	Output get();

	@Override
	default Output apply(Void __) {
		return get();
	}

	static <Output> SuperSupplier<Output> from (Supplier<Output> supplier) {
		Objects.requireNonNull(supplier);
		return (SuperSupplier<Output>) supplier;
	}
}
