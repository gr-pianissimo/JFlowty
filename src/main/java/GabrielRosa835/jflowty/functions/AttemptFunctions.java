package GabrielRosa835.jflowty.functions;

import GabrielRosa835.jflowty.functions.extensions.checked.*;
import GabrielRosa835.jflowty.types.*;

import java.util.function.*;

import static GabrielRosa835.jflowty.functions.extensions.FunctionsUtils.*;
import static GabrielRosa835.jflowty.functions.extensions.checked.CheckedFunction.*;
import static GabrielRosa835.jflowty.functions.extensions.conversions.RunnableFunction.*;
import static java.util.function.Function.*;

public final class AttemptFunctions {
	private AttemptFunctions() {
		throw new AssertionError("No instances for you!");
	}

	public static <I, O, X extends Exception> Function<I, Attempt<O, X>> successOf(Function<I, O> resultMapper) {
		return resultMapper.andThen(Attempt::success);
	}
	public static <I, O, X extends Exception> Function<I, Attempt<O, X>> failureOf(Function<I, X> exceptionMapper) {
		return exceptionMapper.andThen(Attempt::failure);
	}

	@SuppressWarnings("unchecked") public static <I, O, X extends Exception> Function<I, Attempt<O, X>> attemptOf(
			CheckedFunction<I, O, X> checkedFunction,
			Function<O, Attempt<O, X>> resultMapper,
			Function<X, Attempt<O, X>> exceptionMapper
	) {
		return input -> {
			try {
				return checkedFunction.andThen(resultMapper).apply(input);
			} catch (Exception e) {
				return exceptionMapper.apply((X) e);
			}
		};
	}

	public static <I, O, X extends Exception> Function<I, Attempt<O, X>> attemptOf(
			CheckedFunction<I, O, X> checkedFunction,
			Function<X, Attempt<O, X>> exceptionMapper
	) {
		return attemptOf(checkedFunction, Attempt::success, exceptionMapper);
	}

	public static <I, O, X extends Exception> Function<I, Attempt<O, X>> attemptOf(
			CheckedFunction<I, O, X> checkedFunction
	) {
		return attemptOf(checkedFunction, Attempt::success, Attempt::failure);
	}

	public static <I, O, X extends Exception> Function<I, Attempt<O, X>> attemptOf(
			Function<I, O> function
	) {
		return attemptOf(mockThrowing(function));
	}

	public static <T, X extends Exception> Function<Attempt<T, X>, Group<T, X>> toLeftGroup() {
		return attempt -> attempt.either(Group::left, Group::right);
	}

	public static <T, X extends Exception> Function<Attempt<T, X>, Group<X, T>> toRightGroup() {
		return attempt -> attempt.either(Group::right, Group::left);
	}

	public static <T, X extends Exception> Function<Attempt<T, X>, Nullable<T>> toNullable() {
		return attempt -> attempt.either(Nullable::of, Nullable::empty);
	}

	public static <I, O, X extends Exception> Function<I, Attempt<O, X>> map(Function<I, O> mapper) {
		return attemptOf(mapper);
	}

	public static <I, O, XI extends Exception, XO extends Exception> Function<Attempt<I, XI>, Attempt<O, XO>> flatMap(Function<I, Attempt<O, XO>> mapper,
			Function<XI, XO> exceptionMapper
	) {
		return attempt -> attempt.either(mapper, exceptionMapper.andThen(Attempt::failure));
	}

	public static <I, O, X extends Exception> Function<Attempt<I, X>, Attempt<O, X>> flatMap(
			Function<I, Attempt<O, X>> mapper
	) {
		return flatMap(mapper, identity());
	}

	public static <T, X extends Exception> Function<Attempt<T, X>, T> getOr(T other) {
		return attempt -> attempt.either(identity(), __ -> other);
	}

	public static <T, X extends Exception> Function<Attempt<T, X>, T> getOrFrom(Supplier<T> otherSupplier) {
		return attempt -> attempt.either(identity(), __ -> otherSupplier.get());
	}

	public static <T, X extends Exception> Function<Attempt<T, X>, T> getOrRecover(Function<X, T> recoveryFunction) {
		return attempt -> attempt.either(identity(), recoveryFunction);
	}

	public static <T, X extends RuntimeException> Function<Attempt<T, X>, T> getOrThrow() {
		return attempt -> attempt.either(identity(), x -> {throw x;});
	}

	public static <T, X extends RuntimeException> Function<Attempt<T, X>, T> getOrThrow(X exception) {
		return attempt -> attempt.either(identity(), __ -> {throw exception;});
	}

	public static <T, X extends RuntimeException> Function<Attempt<T, X>, T> getOrThrow(Supplier<X> exceptionSupplier) {
		return attempt -> attempt.either(identity(), __ -> {throw exceptionSupplier.get();});
	}

	public static <T, XI extends Exception, XO extends RuntimeException> Function<Attempt<T, XI>, T> getOrThrow(Function<XI, XO> exceptionMapper) {
		return attempt -> attempt.either(identity(), x -> {throw exceptionMapper.apply(x);});
	}

	public static <T1, T2, X extends RuntimeException> Function<Attempt<T2, Attempt<T1, X>>, Attempt<T1, X>> mergeSuccess(
			Function<T2, T1> successMapper
	) {
		return attempt -> attempt.either(successMapper.andThen(Attempt::success), identity());
	}

	public static <L, X1 extends Exception, X2 extends Exception> Function<Attempt<Attempt<L, X1>, X2>, Attempt<L, X1>> mergeFailure(
			Function<X2, X1> exceptionMapper
	) {
		return attempt -> attempt.either(identity(), exceptionMapper.andThen(Attempt::failure));
	}

	public static <T> Function<T, Attempt<T, Exception>> filter(Predicate<T> predicate) {
		return value -> predicate.test(value) ? Attempt.success(value) : Attempt.failure(new Exception(
				"Filtering test for predicate " + predicate + " failed"));
	}

	public static <T, X extends Exception> Function<T, Attempt<T, X>> filter(Predicate<T> predicate, X exception) {
		return value -> predicate.test(value) ? Attempt.success(value) : Attempt.failure(exception);
	}

	public static <T, X extends Exception> Function<T, Attempt<T, X>> filter(
			Predicate<T> predicate,
			Supplier<X> exceptionSupplier
	) {
		return value -> predicate.test(value) ? Attempt.success(value) : Attempt.failure(exceptionSupplier.get());
	}

	public static <T, X extends Exception> Function<T, Attempt<T, X>> filter(
			Predicate<T> predicate,
			Function<T, X> exceptionMapper
	) {
		return value -> predicate.test(value) ? Attempt.success(value) : Attempt.failure(exceptionMapper.apply(value));
	}

	public static <T, X extends Exception> Function<Attempt<T, X>, Attempt<T, X>> ifSuccessDo(Consumer<T> consumer) {
		return attempt -> attempt.either(attemptOf(peek(consumer)), Attempt::failure);
	}

	public static <T, X extends Exception> Function<Attempt<T, X>, Attempt<T, X>> ifEmptyDo(Runnable runnable) {
		return attempt -> attempt.either(Attempt::success, toFunction(runnable));
	}
}
