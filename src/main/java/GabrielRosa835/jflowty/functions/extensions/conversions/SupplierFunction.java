package GabrielRosa835.jflowty.functions.extensions.conversions;

import java.util.function.*;

@FunctionalInterface
public interface SupplierFunction<Input, Output> extends Function<Input, Output>, Supplier<Output> {
	@Override
	Output get();

	@Override
	default Output apply(Input __) {
		return get();
	}

	static <Input, Output> SupplierFunction<Input, Output> toFunction (Supplier<Output> supplier) {
		return (SupplierFunction<Input, Output>) supplier;
	}
}
