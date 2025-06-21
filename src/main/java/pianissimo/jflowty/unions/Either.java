package pianissimo.jflowty.unions;

import pianissimo.jflowty.functions.conversions.*;

import java.util.*;
import java.util.function.*;

import static pianissimo.jflowty.functions.conversions.SuperConsumer.peek;
import static pianissimo.jflowty.functions.conversions.SuperFunction.identity;

public abstract class Either<TLeft, TRight> {

	protected Either () {}

	public abstract <T, T1 extends T, T2 extends T> T either (
			SuperFunction<TLeft, T1> onLeft,
			SuperFunction<TRight, T2> onRight
	);

	@SuppressWarnings ("unchecked")
	public <T, T2 extends T, E extends Either<TLeft, TRight>> T then (SuperFunction<E, T2> mapper) {
		return mapper.apply((E) this);
	}

	public boolean isLeft () {
		return Left.class.isAssignableFrom(this.getClass());
	}

	public boolean isRight () {
		return Right.class.isAssignableFrom(this.getClass());
	}

	public <LO> Either<LO, TRight> mapLeft (SuperFunction<TLeft, LO> onLeft) {
		return either(left -> left(onLeft.apply(left)), Either::right);
	}

	public <RO> Either<TLeft, RO> mapRight (SuperFunction<TRight, RO> onRight) {
		return either(Either::left, right -> right(onRight.apply(right)));
	}

	public <LO> Either<LO, TRight> flatMapLeft (SuperFunction<TLeft, Either<LO, TRight>> onLeft) {
		return either(onLeft, Either::right);
	}

	public <RO> Either<TLeft, RO> flatMapRight (SuperFunction<TRight, Either<TLeft, RO>> onRight) {
		return either(Either::left, onRight);
	}

	public TLeft getLeft () {
		return either(identity(), __ -> {throw new UnionTypeException(this);});
	}

	public TLeft getLeftOr (TLeft otherLeft) {
		return either(identity(), __ -> otherLeft);
	}

	public TLeft getLeftOrFrom (SuperSupplier<TLeft> otherLeftSupplier) {
		return either(identity(), __ -> otherLeftSupplier.get());
	}

	public TLeft getLeftOrRecover (SuperFunction<TRight, TLeft> mergingFunction) {
		return either(identity(), mergingFunction);
	}

	public TRight getRight () {
		return either(__ -> {throw new UnionTypeException(this);}, identity());
	}

	public TRight getRightOr (TRight otherRight) {
		return either(__ -> otherRight, identity());
	}

	public TRight getRightOrFrom (SuperSupplier<TRight> otherRightSupplier) {
		return either(__ -> otherRightSupplier.get(), identity());
	}

	public TRight getRightOrRecover (SuperFunction<TLeft, TRight> mergingFunction) {
		return either(mergingFunction, identity());
	}

	public Either<TLeft, TRight> onEitherPeek (SuperConsumer<TLeft> leftConsumer, SuperConsumer<TRight> rightConsumer) {
		return either(left -> left(peek(leftConsumer, left)), right -> right(peek(rightConsumer, right)));
	}

	public Either<TLeft, TRight> onLeftPeek (SuperConsumer<TLeft> leftConsumer) {
		return either(left -> left(peek(leftConsumer, left)), Either::right);
	}

	public Either<TLeft, TRight> onRightPeek (SuperConsumer<TRight> rightConsumer) {
		return either(Either::left, right -> right(peek(rightConsumer, right)));
	}

	public void onEitherDo (SuperConsumer<TLeft> leftConsumer, SuperConsumer<TRight> rightConsumer) {
		either(leftConsumer, rightConsumer);
	}

	public void onLeftDo (SuperConsumer<TLeft> leftConsumer) {
		either(leftConsumer, __ -> null);
	}

	public void onRightDo (SuperConsumer<TRight> rightConsumer) {
		either(__ -> null, rightConsumer);
	}

	public Either<TRight, TLeft> invert () {
		return either(Either::right, Either::left);
	}

	public Either<TLeft, TRight> filterLeft (
			Predicate<TLeft> predicate,
			SuperFunction<TLeft, TRight> onFalse
	) {
		return either(
				left -> predicate.test(left) ? left(left) : right(onFalse.apply(left)),
				Either::right
		);
	}

	public Either<TLeft, TRight> filterRight (
			Predicate<TRight> predicate,
			SuperFunction<TRight, TLeft> onFalse
	) {
		return either(
				Either::left,
				right -> predicate.test(right) ? right(right) : left(onFalse.apply(right))
		);
	}

	public static <TLeft, TRight> Either<TLeft, TRight> left (TLeft left) {
		return new Either.Left<>(left);
	}

	public static <TLeft, TRight> Either<TLeft, TRight> right (TRight right) {
		return new Either.Right<>(right);
	}

	static class Left<TLeft, TRight> extends Either<TLeft, TRight> {
		private final TLeft value;

		private Left (TLeft value) {
			this.value = value;
		}

		@Override
		public <T, T1 extends T, T2 extends T> T either (
				SuperFunction<TLeft, T1> onLeft,
				SuperFunction<TRight, T2> onRight
		) {
			return onLeft.apply(value);
		}

		@Override public String toString () {
			return String.format("Left(%s)", value);
		}

		@Override public boolean equals (Object o) {
			if (o == null || getClass() != o.getClass()) return false;
			Left<?, ?> left = (Left<?, ?>) o;
			return Objects.equals(value, left.value);
		}

		@Override public int hashCode () {
			return Objects.hashCode(value);
		}
	}

	private static class Right<TLeft, TRight> extends Either<TLeft, TRight> {
		private final TRight value;

		private Right (TRight value) {
			this.value = value;
		}

		@Override
		public <T, T1 extends T, T2 extends T> T either (
				SuperFunction<TLeft, T1> onLeft,
				SuperFunction<TRight, T2> onRight
		) {
			return onRight.apply(value);
		}

		@Override public String toString () {
			return String.format("Right(%s)", value);
		}

		@Override public boolean equals (Object o) {
			if (o == null || getClass() != o.getClass()) return false;
			Right<?, ?> right = (Right<?, ?>) o;
			return Objects.equals(value, right.value);
		}

		@Override public int hashCode () {
			return Objects.hashCode(value);
		}
	}
}
