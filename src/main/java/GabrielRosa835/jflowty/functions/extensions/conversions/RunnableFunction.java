package GabrielRosa835.jflowty.functions.extensions.conversions;

import java.util.function.*;

@FunctionalInterface
public interface RunnableFunction<Input, Output> extends Function<Input, Output>, Runnable, Supplier<Output>, Consumer<Input> {
	@Override void run();

	@Override default Output apply(Input __) {
		run();
		return null;
	}
	@Override default Output get() {
		run();
		return null;
	}
	@Override default void accept(Input __) {
		run();
	}

	@SuppressWarnings("unchecked")
	static <Input, Output> RunnableFunction<Input, Output> toFunction(Runnable runnable) {
		return (RunnableFunction<Input, Output>) runnable;
	}
}
