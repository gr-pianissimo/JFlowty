package GabrielRosa835.jflowty.functions.extensions;

import java.util.function.*;

public final class FunctionsUtils {
	private FunctionsUtils() {
		throw new AssertionError("No instances for you!");
	}

	public static <R, T extends R> Function<T, R> upCast() {
		return input -> input;
	}

	public static <R, T extends R> Function<T, R> upCastTo(Class<T> type) {
		return input -> input;
	}

	@SuppressWarnings("unchecked")
	public static <T, R extends T> Function<T, R> downCast() {
		return input -> (R) input;
	}

	@SuppressWarnings("unchecked")
	public static <T, R extends T> Function<T, R> downCastTo(Class<T> type) {
		return input -> (R) input;
	}

	public static <I, O> Function<I, O> nullify() {
		return input -> null;
	}

	public static <I, O> Function<I, O> nullifyTo(Class<O> outputType) {
		return input -> null;
	}

	public static <A, B, R> Function<BiFunction<A, B, R>, BiFunction<B, A, R>> flip() {
		return input -> (a, b) -> input.apply(b, a);
	}

	@SuppressWarnings("unchecked")
	public static <T> Function<T, T> peek(Consumer<T> consumer) {
		return input -> peek(consumer, input);
	}

	public static <T, O extends T> O peek(Consumer<T> consumer, O value) {
		consumer.accept(value);
		return value;
	}

	public static <T> Function<Predicate<T>, Predicate<T>> negate() {
		return Predicate::negate;
	}

	public static <I, M, O> Function<Function<M, O>, Function<I, O>> composeWith(Function<I, M> firstFunction) {
		return secondFunction -> secondFunction.compose(firstFunction);
	}

	public static <I, M, O> Function<Function<I, M>, Function<I, O>> continueWith(Function<M, O> secondFunction) {
		return firstFunction -> firstFunction.andThen(secondFunction);
	}
}
