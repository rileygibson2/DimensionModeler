package particlesystems;

public class Wave {
	public enum Mode {X, Y, Diag1, Diag2};
	public Mode mode;
	public double i;
	public double inc;
	public double amp;
	public double decayRate;
	public boolean infinite;
	
	public Wave(double i, double inc, double amp, double decayRate, Mode mode, boolean infinite) {
		this.mode = mode;
		this.i = i;
		this.inc = inc;
		this.amp = amp;
		this.decayRate = decayRate;
		this.infinite = infinite;
	}
}
