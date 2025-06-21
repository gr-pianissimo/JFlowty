package pianissimo.jflowty.functions.conversions;

import java.util.Objects;
import java.util.function.*;

@FunctionalInterface
public interface SuperFunction<Input, Output> extends Function<Input, Output> {

	@Override Output apply(Input t);

	default <V> Function<V, Output> compose(SuperFunction<? super V, ? extends Input> before) {
		Objects.requireNonNull(before);
		return (V v) -> apply(before.apply(v));
	}

	default <V> Function<Input, V> andThen(SuperFunction<? super Output, ? extends V> after) {
		Objects.requireNonNull(after);
		return (Input t) -> after.apply(apply(t));
	}

	static <Input> SuperFunction<Input, Input> identity() {
		return t -> t;
	}

	static <Input, Output> SuperFunction from (Function<Input, Output> function) {
		Objects.requireNonNull(function);
		return (SuperFunction<Input, Output>) function;
	}
}
