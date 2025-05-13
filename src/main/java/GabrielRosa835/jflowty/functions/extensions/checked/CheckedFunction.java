package GabrielRosa835.jflowty.functions.extensions.checked;

import java.util.function.*;

@FunctionalInterface
public interface CheckedFunction<Input, Output, X extends Exception> {
	Output apply(Input input) throws X;

	default <T> CheckedFunction<Input, T, X> andThen(Function<Output, T> next) {
		return input -> next.apply(this.apply(input));
	}

	default <T> CheckedFunction<T, Output, X> compose(Function<T, Input> next) {
		return input -> this.apply(next.apply(input));
	}

	static <Input, Output, X extends Exception> Function<Input, Output> throwingRuntime(CheckedFunction<Input, Output, X> checkedFunction) {
		return input -> {
			try {
				return checkedFunction.apply(input);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}

	static <Input, Output, X extends Exception> CheckedFunction<Input, Output, X> mockThrowing(Function<Input, Output> function) {
		return function::apply;
	}
}
