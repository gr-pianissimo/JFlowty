package GabrielRosa835.jflowty.types;

import java.util.*;
import java.util.function.*;

public abstract class Nullable<T> extends Group<T, T> {

	protected Nullable() {}

	public boolean isPresent() {
		return this instanceof Nullable.Full;
	}

	public boolean isEmpty() {
		return this instanceof Nullable.Empty;
	}

	public static <T> Nullable<T> of(T value) {
		if (value != null) {
			return new Nullable.Full<>(value);
		}
		return new Nullable.Empty<>();
	}

	public static <T> Nullable<T> empty() {
		return new Nullable.Empty<>();
	}

	public static <T, O> Nullable<O> empty(T __) {
		return new Nullable.Empty<>();
	}

	public static <T> Nullable<T> empty(Class<T> type) {
		return new Nullable.Empty<>();
	}

	private static class Full<T> extends Nullable<T> {
		private final T value;

		private Full(T value) {
			this.value = value;
		}

		@Override public <U, U1 extends U, U2 extends U> U either(Function<T, U1> onFull, Function<T, U2> __) {
			return onFull.apply(value);
		}

		public String toString() {
			return "Full[" + value + ']';
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else if (o != null && this.getClass() == o.getClass()) {
				Nullable.Full<?> that = (Nullable.Full) o;
				return Objects.equals(value, that.value);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(value);
		}
	}

	private static class Empty<T> extends Nullable<T> {
		private Empty() {}

		@Override public <U, U1 extends U, U2 extends U> U either(Function<T, U1> __, Function<T, U2> onEmpty) {
			return onEmpty.apply(null);
		}

		public String toString() {
			return "Empty";
		}

		public boolean equals(Object o) {
			return this == o;
		}

		public int hashCode() {
			return 1;
		}
	}
}
