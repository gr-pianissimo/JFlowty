package GabrielRosa835.jflowty.functions.extensions.conversions;

import java.util.function.*;

@FunctionalInterface
public interface PredicateFunction<Input> extends Function<Input, Boolean>, Predicate<Input>, Supplier<Boolean> {
	@Override
	boolean test(Input input);

	@Override
	default Boolean apply(Input input) {
		return test(input);
	}

	@Override
	default Boolean get() {
		return test(null);
	}

	static <Input> PredicateFunction<Input> toFunction(Predicate<Input> predicate) {
		return (PredicateFunction<Input>) predicate;
	}
}
