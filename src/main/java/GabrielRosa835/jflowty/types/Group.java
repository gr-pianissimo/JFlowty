package GabrielRosa835.jflowty.types;

import GabrielRosa835.jflowty.functions.*;

import java.util.*;
import java.util.function.*;

/**
 * A functional container representing a value of one of two possible types (Left or Right).
 * <p>
 * Used for modeling disjoint unions (either one type of result {@code Right<R>} or another {@code Left<L>}).
 * Inspired by functional programming constructs like Scala's {@code Either} or Vavr's {@code Either}.
 * </p>
 *
 * <h3>Examples:</h3>
 * <pre>{@code
 * Group<String, Integer> result = Group.right(69);
 * String output = result.either(
 *     left -> "Error: " + left,
 *     right -> "Value: " + right
 * ); // Returns "Value: 69"
 * }</pre>
 *
 * @param <L> the type of the {@code Left} value
 * @param <R> the type of the {@code Right} value
 * @see GroupFunctions
 * @since 1.0
 */
public abstract class Group<L, R> {

	protected Group() {}

	/**
	 * Checks if this instance is a {@code Left} value.
	 *
	 * @return {@code true} if this is a {@code Left}, {@code false} otherwise.
	 */
	public boolean isLeft() {
		return this instanceof Group.Left;
	}

	/**
	 * Checks if this instance is a {@code Right} value.
	 *
	 * @return {@code true} if this is a {@code Right}, {@code false} otherwise.
	 */
	public boolean isRight() {
		return this instanceof Group.Right;
	}

	/**
	 * Applies one of two functions based on whether this is a {@code Left} or {@code Right}.
	 *
	 * @param onLeft  the function to apply if this is a {@code Left} (must not be null).
	 * @param onRight the function to apply if this is a {@code Right} (must not be null).
	 * @param <T>     the common return type of both functions.
	 * @return the result of applying the matching function.
	 * @throws NullPointerException if either function is null.
	 */
	public abstract <T, T1 extends T, T2 extends T> T either(Function<L, T1> onLeft, Function<R, T2> onRight);

	/**
	 * Applies the input function to the current group and returns the result.
	 * <p>
	 *    Allows the inline usage of multiple manipulation functions.
	 * </p>
	 * <h3>Example:</h3>
	 * <pre>{@code
	 * Group<String, String> result = Group.right(69)
	 * 	.then(onRight(add(42)))
	 * 	.then(map(i -> Integer.toString(i))
	 * 	.then(print())
	 * }</pre>
	 *
	 * @param mapper  the function to apply to the group element.
	 * @param <T>     the return type of the input function (extended as T2 for better type handling)
	 * @return the result of applying the mapping function.
	 * @throws NullPointerException if the function is null.
	 * @see GroupFunctions
	 */
	public <T, T2 extends T> T then(Function<Group<L, R>, T2> mapper) {
		return mapper.apply(this);
	}

	/**
	 * Creates a {@code Left} value.
	 *
	 * @param left the value to wrap (may be null).
	 * @param <L>  the left type.
	 * @param <R>  the right type.
	 * @return a new {@code Left} instance.
	 */
	public static <L, R> Group<L, R> left(L left) {
		return new Group.Left<>(left);
	}

	public static <L, R> Group<L, R> left(L left, Class<R> rightType) {
		return new Group.Left<>(left);
	}

	public static <L, R> Group<L, R> right(R right) {
		return new Group.Right<>(right);
	}

	public static <L, R> Group<L, R> right(R right, Class<L> leftType) {
		return new Group.Right<>(right);
	}

	private static class Left<L, R> extends Group<L, R> {
		private final L value;

		private Left(L value) {
			this.value = value;
		}

		@Override public <T, T1 extends T, T2 extends T> T either(Function<L, T1> onLeft, Function<R, T2> __) {
			return onLeft.apply(value);
		}

		public String toString() {
			return "Left[" + value.toString() + ']';
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (o != null && this.getClass() == o.getClass()) {
				Group.Left<?, ?> that = (Group.Left) o;
				return Objects.equals(value, that.value);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(value);
		}
	}

	private static class Right<L, R> extends Group<L, R> {
		private final R value;

		private Right(R value) {
			this.value = value;
		}

		@Override public <T, T1 extends T, T2 extends T> T either(Function<L, T1> __, Function<R, T2> onRight) {
			return onRight.apply(value);
		}

		public String toString() {
			return "Right[" + value + ']';
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (o != null && this.getClass() == o.getClass()) {
				Group.Right<?, ?> that = (Group.Right) o;
				return Objects.equals(value, that.value);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(value);
		}
	}
}
