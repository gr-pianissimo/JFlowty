package GabrielRosa835.jflowty.functions;

import GabrielRosa835.jflowty.functions.extensions.checked.*;
import GabrielRosa835.jflowty.types.*;

import java.util.function.*;

import static GabrielRosa835.jflowty.functions.AttemptFunctions.failureOf;
import static GabrielRosa835.jflowty.functions.NullableFunctions.nullableOf;
import static GabrielRosa835.jflowty.functions.extensions.FunctionsUtils.*;
import static java.util.function.Function.*;

/**
 * Utility functions for working with {@link Group}, enabling functional composition and transformation.
 * <p>
 * Provides methods for mapping, flat-mapping, filtering, and converting {@code Group} instances.
 * All methods are pure functions (no side effects).
 * </p>
 *
 * <h3>Examples:</h3>
 * <pre>{@code
 * Group<String, Integer> result = Group.right(69);
 * Function<Group<String, Integer>, String> toMessage =
 *     GroupFunctions.getRightOr("Default");
 * String output = toMessage.apply(result); // Returns "69"
 * }</pre>
 *
 * @see Group
 * @since 1.0
 */
public final class GroupFunctions {
	private GroupFunctions() {
		throw new AssertionError("No instances for you!");
	}

	/**
	 * Converts a function {@code I -> L} into a function {@code I -> Group<L, R>} (wrapped in {@code Left}).
	 *
	 * @param function the input function (must not be null).
	 * @param <I>      the input type.
	 * @param <L>      the left type.
	 * @param <R>      the right type (unused but preserved for symmetry).
	 * @return a new function that wraps results in {@code Left}.
	 * @throws NullPointerException if the input function is null.
	 */
	public static <I, L, R> Function<I, Group<L, R>> leftOf(Function<I, L> function) {
		return function.andThen(Group::left);
	}

	public static <I, L, R> Function<I, Group<L, R>> rightOf(Function<I, R> function) {
		return function.andThen(Group::right);
	}

	public static <L, R, T, T1 extends T, T2 extends T> Function<Group<L, R>, T> either(
			Function<L, T1> onLeft,
			Function<R, T2> onRight
	) {
		return group -> group.either(onLeft, onRight);
	}

	public static <L, R> Function<Group<L, R>, Group<L, R>> print() {
		return onEitherDo(System.out::println);
	}

	/**
	 * Converts a {@code Group<L, R>} into a nullable value, recovering from {@code Right} if needed.
	 *
	 * @param rightRecover a function to convert {@code Right} values into {@code Left} (null if unused).
	 * @param <L>          the left type.
	 * @param <R>          the right type.
	 * @return a function that always returns a {@code Nullable<L>}.
	 * @see Nullable
	 */
	public static <L, R> Function<Group<L, R>, Nullable<L>> leftToNullable(Function<R, L> rightRecover) {
		return either(Nullable::of, nullableOf(rightRecover));
	}

	public static <L, R> Function<Group<L, R>, Nullable<L>> leftToNullable() {
		return either(Nullable::of, Nullable::empty);
	}

	public static <L, R> Function<Group<L, R>, Nullable<R>> rightToNullable(Function<L, R> leftRecover) {
		return either(nullableOf(leftRecover), Nullable::of);
	}

	public static <L, R> Function<Group<L, R>, Nullable<R>> rightToNullable() {
		return either(Nullable::empty, Nullable::of);
	}

	public static <L, R, X extends Exception> Function<Group<L, R>, Attempt<L, X>> leftToAttempt(Function<R, X> exceptionMapper) {
		return either(Attempt::success, failureOf(exceptionMapper));
	}

	public static <L, R, X extends Exception> Function<Group<L, R>, Attempt<R, X>> rightToAttempt(Function<L, X> exceptionMapper) {
		return either(failureOf(exceptionMapper), Attempt::success);
	}

	public static <LI, LO, RI, RO> Function<Group<LI, RI>, Group<LO, RO>> map(
			Function<LI, LO> onLeft,
			Function<RI, RO> onRight
	) {
		return either(leftOf(onLeft), rightOf(onRight));
	}

	public static <LI, LO, RI, RO> Function<Group<LI, RI>, Group<LO, RO>> flatMap(
			Function<LI, Group<LO, RO>> onLeft,
			Function<RI, Group<LO, RO>> onRight
	) {
		return either(onLeft, onRight);
	}

	public static <LI, LO, R> Function<Group<LI, R>, Group<LO, R>> mapLeft(Function<LI, LO> mapper) {
		return map(mapper, identity());
	}

	public static <L, RI, RO> Function<Group<L, RI>, Group<L, RO>> mapRight(Function<RI, RO> mapper) {
		return map(identity(), mapper);
	}

	public static <LI, LO, R> Function<Group<LI, R>, Group<LO, R>> flatMapLeft(Function<LI, Group<LO, R>> mapper) {
		return flatMap(mapper, Group::right);
	}

	public static <L, RI, RO> Function<Group<L, RI>, Group<L, RO>> flatMapRight(Function<RI, Group<L, RO>> mapper) {
		return flatMap(Group::left, mapper);
	}

	public static <L, R> Function<Group<L, R>, L> getLeftOr(L other) {
		return either(identity(), right -> other);
	}

	public static <L, R> Function<Group<L, R>, L> getLeftOrFrom(Supplier<L> otherSupplier) {
		return either(identity(), right -> otherSupplier.get());
	}

	public static <L, R> Function<Group<L, R>, L> getLeftOrRecover(Function<R, L> mergingFunction) {
		return either(identity(), mergingFunction);
	}

	public static <L, R> Function<Group<L, R>, R> getRightOr(R other) {
		return either(left -> other, identity());
	}

	public static <L, R> Function<Group<L, R>, R> getRightOrFrom(Supplier<R> otherSupplier) {
		return either(left -> otherSupplier.get(), identity());
	}

	public static <L, R> Function<Group<L, R>, R> getRightOrRecover(Function<L, R> mergingFunction) {
		return either(mergingFunction, identity());
	}

	public static <L1, L2, R> Function<Group<L2, Group<L1, R>>, Group<L1, R>> mergeLeft(Function<L2, L1> mapper) {
		return either(leftOf(mapper), identity());
	}

	public static <L, R1, R2> Function<Group<Group<L, R1>, R2>, Group<L, R1>> mergeRight(Function<R2, R1> mapper) {
		return either(identity(), rightOf(mapper));
	}

	public static <T, L extends T, R extends T> Function<Group<L, R>, Group<L, R>> onEitherDo(Consumer<T> commonConsumer) {
		return group -> group.either(
				left -> Group.left(peek(commonConsumer, left)),
				right -> Group.right(peek(commonConsumer, right))
		);
	}

	public static <L, R> Function<Group<L, R>, Group<L, R>> onEitherDo(
			Consumer<L> leftConsumer,
			Consumer<R> rightConsumer
	) {
		return either(leftOf(peek(leftConsumer)), rightOf(peek(rightConsumer)));
	}

	public static <L, R> Function<Group<L, R>, Group<L, R>> onLeftDo(Consumer<L> consumer) {
		return either(leftOf(peek(consumer)), rightOf(identity()));
	}

	public static <L, R> Function<Group<L, R>, Group<L, R>> onRightDo(Consumer<R> consumer) {
		return either(leftOf(identity()), rightOf(peek(consumer)));
	}

	@SuppressWarnings("unchecked") public static <LI, LO, R, X extends Exception> Function<LI, Group<LO, R>> onLeftTry(
			CheckedFunction<LI, LO, X> onLeft,
			Function<X, R> onException
	) {
		return input -> {
			try {
				return Group.left(onLeft.apply(input));
			} catch (Exception e) {
				return Group.right(onException.apply((X) e));
			}
		};
	}

	@SuppressWarnings("unchecked") public static <L, RI, RO, X extends Exception> Function<RI, Group<L, RO>> onRightTry(
			CheckedFunction<RI, RO, X> onRight,
			Function<X, L> onException
	) {
		return input -> {
			try {
				return Group.right(onRight.apply(input));
			} catch (Exception e) {
				return Group.left(onException.apply((X) e));
			}
		};
	}

	public static <LI, LO, RI, RO, XL extends Exception, XR extends Exception> Function<Group<LI, RI>, Group<LO, RO>> onEitherTry(
			CheckedFunction<LI, LO, XL> onLeft,
			Function<XL, RO> onLeftException,
			CheckedFunction<RI, RO, XR> onRight,
			Function<XR, LO> onRightException
	) {
		return input -> input.either(onLeftTry(onLeft, onLeftException), onRightTry(onRight, onRightException));
	}

	public static <L, R> Function<Group<L, R>, Group<R, L>> invert() {
		return input -> input.either(Group::right, Group::left);
	}

	public static <L, R> Function<L, Group<L, R>> filterLeft(Predicate<L> predicate, Function<L, R> onFalse) {
		return left -> predicate.test(left) ? Group.left(left) : Group.right(onFalse.apply(left));
	}

	public static <L, R> Function<R, Group<L, R>> filterRight(Predicate<R> predicate, Function<R, L> onFalse) {
		return right -> predicate.test(right) ? Group.right(right) : Group.left(onFalse.apply(right));
	}
}
