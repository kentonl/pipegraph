package edu.uw.pipegraph.util.tuple;

import java.io.Serializable;
import java.util.Objects;

public class Triplet<A, B, C> implements Serializable {
	private final A	first;
	private final B	second;
	private final C	third;

	private Triplet(A first, B second, C third) {
		this.first = first;
		this.second = second;
		this.third = third;
	}

	public static <A, B, C> Triplet<A, B, C> of(A first, B second, C third) {
		return new Triplet<>(first, second, third);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Triplet) {
			return Objects.equals(this.first, ((Triplet<?, ?, ?>) other).first)
					&& Objects.equals(this.second,
							((Triplet<?, ?, ?>) other).second)
					&& Objects.equals(this.third,
							((Triplet<?, ?, ?>) other).third);
		} else {
			return false;
		}
	}

	public A first() {
		return first;
	}

	@Override
	public int hashCode() {
		return Objects.hash(first, second, third);
	}

	public B second() {
		return second;
	}

	public C third() {
		return third;
	}

	@Override
	public String toString() {
		return "(" + first + "," + second + "," + third + ")";
	}

}