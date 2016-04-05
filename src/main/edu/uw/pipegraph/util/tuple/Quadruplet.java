package edu.uw.pipegraph.util.tuple;

import java.io.Serializable;
import java.util.Objects;

public class Quadruplet<A, B, C, D> implements Serializable {
	private final A	first;
	private final B	second;
	private final C	third;
	private final D	fourth;
	private final int hashCode;

	private Quadruplet(A first, B second, C third, D fourth) {
		this.first = first;
		this.second = second;
		this.third = third;
		this.fourth = fourth;
		this.hashCode = Objects.hash(first, second, third, fourth);
	}

	public static <A, B, C, D> Quadruplet<A, B, C, D> of(A first, B second, C third, D fourth) {
		return new Quadruplet<>(first, second, third, fourth);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof Quadruplet) {
			return Objects.equals(this.first, ((Quadruplet<?, ?, ?, ?>) other).first)
					&& Objects.equals(this.second,
							((Quadruplet<?, ?, ?, ?>) other).second)
					&& Objects.equals(this.third,
							((Quadruplet<?, ?, ?, ?>) other).third)
					&& Objects.equals(this.fourth,
					((Quadruplet<?, ?, ?, ?>) other).fourth);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	public A first() {
		return first;
	}

	public B second() {
		return second;
	}

	public C third() {
		return third;
	}

	public D fourth() {
		return fourth;
	}

	@Override
	public String toString() {
		return "(" + first + "," + second + "," + third + "," + fourth +")";
	}

}