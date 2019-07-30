namespace Sample
{
    open Microsoft.Quantum.Canon;
    open Microsoft.Quantum.Intrinsic;

    operation sampleQ1 () : Unit {
        Message("--- sampleQ1");

        using ( (q0, q1) = (Qubit(), Qubit()) ) {

            let r0 = M(q0);
            let r1 = M(q1);

            checkResult("q0", r0);
            checkResult("q1", r1);

            Reset(q0);
            Reset(q1);
        }
    }

    operation sampleQ2 () : Unit {
        Message("--- sampleQ2");

        using ( (q0, q1) = (Qubit(), Qubit()) ) {
            H(q0);
            CNOT(q0, q1);

            let r0 = M(q0);
            let r1 = M(q1);

            checkResult("q0", r0);
            checkResult("q1", r1);

            Reset(q0);
            Reset(q1);
        }
    }

    operation sampleQ3 () : Unit {
        Message("--- sampleQ3");

        using ( (q0, q1) = (Qubit(), Qubit()) ) {
            X(q0);
            X(q1);

            H(q0);
            CNOT(q0, q1);

            let r0 = M(q0);
            let r1 = M(q1);

            checkResult("q0", r0);
            checkResult("q1", r1);

            Reset(q0);
            Reset(q1);
        }
    }

    function checkResult(name: String, res: Result) : Unit {
        if (res == One) {
            Message(name + " = One");
        }
        else {
            Message(name + " = Zero");
        }
    }
}
