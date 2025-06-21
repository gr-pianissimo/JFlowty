package pianissimo.jflowty.functions.conversions;

import java.util.*;
import java.util.function.*;

@FunctionalInterface
public interface SuperConsumer<Input> extends SuperFunction<Input, Void>, Consumer<Input> {
	@Override
	void accept(Input input);

	@Override
	default Void apply(Input input) {
		Objects.requireNonNull(input);
		accept(input);
		return null;
	}

	static <T> SuperFunction<T, T> peek(SuperConsumer<T> consumer) {
		return input -> peek(consumer, input);
	}

	static <T, O extends T> O peek(SuperConsumer<T> consumer, O value) {
		consumer.accept(value);
		return value;
	}

	static <Input> SuperConsumer<Input> from(Consumer<Input> consumer) {
		Objects.requireNonNull(consumer);
		return (SuperConsumer<Input>) consumer;
	}
}