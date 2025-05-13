package GabrielRosa835.jflowty.functions;

import GabrielRosa835.jflowty.functions.extensions.checked.*;
import GabrielRosa835.jflowty.types.*;

import java.util.*;
import java.util.function.*;

import static GabrielRosa835.jflowty.functions.extensions.FunctionsUtils.*;
import static  GabrielRosa835.jflowty.functions.extensions.conversions.RunnableFunction.*;
import static  java.util.function.Function.*;

public final class NullableFunctions {
	private NullableFunctions() {
		throw new AssertionError("No instances for you!");
	}

	public static  <I, O> Function<I, Nullable<O>> nullableOf(Function<I, O> function) {
		return function.andThen(Nullable::of);
	}

	public static  <I, O> Function<I, Nullable<O>> emptyOf(Function<I, O> function) {
		return function.andThen(Nullable::empty);
	}

	public static  <L, R> Function<Nullable<L>, Group<L, R>> toLeftGroup(Supplier<R> rightSupplier) {
		return nullable -> nullable.either(Group::left, empty -> Group.right(rightSupplier.get()));
	}

	public static  <L, R> Function<Nullable<R>, Group<L, R>> toRightGroup(Supplier<L> leftSupplier) {
		return nullable -> nullable.either(Group::right, empty -> Group.left(leftSupplier.get()));
	}

	public static  <T, X extends Exception> Function<Nullable<T>, Attempt<T, X>> toAttempt(X exception) {
		return nullable -> nullable.either(Attempt::success, empty -> Attempt.failure(exception));
	}

	public static  <T, X extends Exception> Function<Nullable<T>, Attempt<T, X>> toAttempt(Supplier<X> exceptionSupplier) {
		return toAttempt(exceptionSupplier.get());
	}

	public static  <T> Function<Optional<T>, Nullable<T>> fromOptional(T other) {
		return input -> Nullable.of(input.orElse(other));
	}
	public static  <T> Function<Optional<T>, Nullable<T>> fromOptional(Supplier<T> onEmpty) {
		return fromOptional(onEmpty.get());
	}
	public static  <T> Function<Optional<T>, Nullable<T>> fromOptional() {
		return fromOptional(() -> null);
	}

	public static  <I, O> Function<I, Nullable<O>> map(Function<I, O> mapper) {
		return nullableOf(mapper);
	}

	public static  <I, O> Function<Nullable<I>, Nullable<O>> flatMap(Function<I, Nullable<O>> mapper) {
		return nullable -> nullable.either(mapper, Nullable::empty);
	}

	public static  <T> Function<Nullable<T>, T> getOr(T other) {
		return nullable -> nullable.either(identity(), __ -> other);
	}

	public static  <T> Function<Nullable<T>, T> getOrFrom(Supplier<T> otherSupplier) {
		return nullable -> nullable.either(identity(), __ -> otherSupplier.get());
	}

	public static  <T, X extends RuntimeException> Function<Nullable<T>, T> getOrThrow(X exception) {
		return nullable -> nullable.either(identity(), __ -> {throw exception;});
	}

	public static  <T, X extends RuntimeException> Function<Nullable<T>, T> getOrThrow(Supplier<X> exceptionSupplier) {
		return nullable -> nullable.either(identity(), __ -> {throw exceptionSupplier.get();});
	}

	public static  <T> Function<Nullable<Nullable<T>>, Nullable<T>> merge() {
		return nullable -> nullable.either(identity(), Nullable::empty);
	}

	public static  <T> Function<T, Nullable<T>> filter(Predicate<T> predicate) {
		return value -> predicate.test(value) ? Nullable.of(value) : Nullable.empty();
	}

	public static  <T> Function<Nullable<T>, Nullable<T>> ifPresentDo(Consumer<T> consumer) {
		return nullable -> nullable.either(nullableOf(peek(consumer)), Nullable::empty);
	}

	public static  <T> Function<Nullable<T>, Nullable<T>> ifEmptyDo(Runnable runnable) {
		return nullable -> nullable.either(Nullable::of, emptyOf(toFunction(runnable)));
	}

	@SuppressWarnings("unchecked")
	public static  <I, O, X extends Exception> Function<I, Nullable<O>> tryTo(
			CheckedFunction<I, O, X> onNullable,
			Function<X, O> recovery
	) {
		return nullable -> {
			try {
				return Nullable.of(onNullable.apply(nullable));
			} catch (Exception e) {
				return Nullable.of(recovery.apply((X) e));
			}
		};
	}

	public static  <I, O, X extends Exception> Function<I, Nullable<O>> tryTo(CheckedFunction<I, O, X> onNullable) {
		return tryTo(onNullable, nullify());
	}
}
