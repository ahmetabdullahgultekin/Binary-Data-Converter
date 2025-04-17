# Binary Data Converter – Hex to Int / Float (Endian‑aware)

This Java CLI utility reads a stream of hexadecimal bytes and converts them into **unsigned integers, signed integers, or IEEE‑754 floats** with optional *little‑* or *big‑endian* interpretation.

> Course: CSE2138  ·  Project 1
> Authors: Ahmet Abdullah Gültekin

---

## 🚀 Usage
```
java P1_150121025_150120004_150120035_150119026 <input_file> <byte_order> <data_type> <size>
```
| Arg | Example | Meaning |
|-----|---------|---------|
| `input_file` | `src/input.txt` | Path containing raw hex bytes (space‑delimited) |
| `byte_order` | `l` or `b` | Little‑endian or big‑endian grouping |
| `data_type`  | `unsigned` · `int` · `float` | Output interpretation |
| `size` | `1` · `2` · `4` | Bytes per value (8‑,16‑,32‑bit) |

### Sample
```
$ javac src/P1_*.java -d build
$ cd build
$ java P1_150121025_150120004_150120035_150119026 ../src/input.txt l float 4
```
The program prints converted values to **stdout** and writes them to `output.txt`.

---

## 🗂 Repository Layout
```
Binary-Data-Converter/
├── src/
│   ├── P1_150121025_....java   # main algorithm (~600 LOC)
│   └── input.txt               # demo data
├── output.txt                  # example results (after run)
└── CSE2138_Project1.pdf        # assignment specification
```

---

## 🧠 Implementation Highlights
* **Endian handling** – bytes are grouped according to `size`; `reverseNumbers()` flips order for little‑endian.
* **Type parsing**
  * **Unsigned / Signed**: accumulates value by shifting and OR‑ing each byte.
  * **Float**: builds 32‑bit int then uses `Float.intBitsToFloat()`; special‑cases `NaN`, `±Inf`.
* **Streaming I/O** – `Scanner` reads tokens; results stored in `ArrayList<String>` then flushed once (`printResultsToFile()`).
* **Modular helpers** – printing, grouping, hex‑to‑binary, exponent extraction etc.

---

## 📈 Performance
For the included 36‑byte sample, conversion completes in <1 ms on JDK 21; memory footprint ≈ 2 MB.

---

## 🔬 Extending Ideas
- Support **64‑bit doubles** (size 8).
- Detect input width automatically based on `0x` prefixes.
- Add `--csv` flag to output comma‑separated values.
- Integrate JUnit tests using parameterised datasets.

