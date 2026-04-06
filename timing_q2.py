import subprocess
import matplotlib.pyplot as plt

N = 15000000  # string length
RUNS = 10     # runs per thread count
THREADS = [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]

avg_times = []

for t in THREADS:
    times = []
    for _ in range(RUNS):
        result = subprocess.run(["./q2", str(t), str(N)], capture_output=True, text=True)
        lines = result.stdout.strip().split("\n")
        ms = float(lines[-1])  # last line is milliseconds
        times.append(ms)
    avg = sum(times) / len(times)
    avg_times.append(avg)
    print(f"t={t}: avg={avg:.3f} ms")

# speedup relative to t=0
base = avg_times[0]
speedups = [base / t for t in avg_times]

fig, (ax1, ax2) = plt.subplots(1, 2, figsize=(12, 5))

ax1.plot(THREADS, avg_times, marker='o')
ax1.set_xlabel("Optimistic threads (t)")
ax1.set_ylabel("Time (ms)")
ax1.set_title("Average matching time vs thread count")
ax1.grid(True)

ax2.plot(THREADS, speedups, marker='o', color='orange')
ax2.axhline(y=1, color='gray', linestyle='--')
ax2.set_xlabel("Optimistic threads (t)")
ax2.set_ylabel("Speedup")
ax2.set_title("Speedup vs thread count")
ax2.grid(True)

plt.tight_layout()
plt.savefig("q2_timing.png")
print("Chart saved to q2_timing.png")
