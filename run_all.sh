#!/bin/bash
# Compiles and runs every task. Usage: bash run_all.sh   (needs JDK 17+)
set -e
cd "$(dirname "$0")"
for t in "Task02 CastingDemo" "Task03 ParkingFacility" "Task06 ConstructorOrderDemo" "Task08 FeeDemo" "Task10 WrapperDemo"; do
  set -- $t
  echo "================ $1 / $2 ================"
  (cd "$1" && javac "$2.java" && java "$2")
done
for d in Task11 Task12; do
  echo "================ $d : compile ================"
  (cd $d && rm -rf out && javac -d out $(find src -name '*.java'))
done
echo "================ Task11 : Main ================";            java -cp Task11/out parking.app.Main
echo "================ Task11 : TestRunner ================";      java -cp Task11/out parking.app.TestRunner
echo "================ Task11 : LookupBenchmark ================"; java -cp Task11/out parking.app.LookupBenchmark
echo "================ Task12 : regression (old tests) ================"; java -cp Task12/out parking.app.TestRunner
echo "================ Task12 : new tests ================";       java -cp Task12/out parking.app.Task12Tests
