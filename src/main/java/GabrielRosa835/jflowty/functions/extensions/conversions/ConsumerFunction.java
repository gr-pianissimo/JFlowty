package GabrielRosa835.jflowty.functions.extensions.conversions;

import java.util.function.*;

@FunctionalInterface
public interface ConsumerFunction<Input, Output> extends Function<Input, Output>, Consumer<Input> {
	@Override
	void accept(Input input);

	@Override
	default Output apply(Input input) {
		accept(input);
		return null;
	}

	static <Input, Output> ConsumerFunction<Input, Output> toFunction(Consumer<Input> consumer) {
		return (ConsumerFunction<Input, Output>) consumer;
	}
}
