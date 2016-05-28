
package main

import (
	"errors"
	"fmt"
	"strconv"
	"github.com/hyperledger/fabric/core/chaincode/shim"
)

type SampleChaincode struct {
}

func (t *SampleChaincode) Init(stub *shim.ChaincodeStub, function string, args []string) ([]byte, error) {

	fmt.Printf("*** SampleChaincode Init \n")

	return nil, nil
}

func (t *SampleChaincode) Invoke(stub *shim.ChaincodeStub, function string, args []string) ([]byte, error) {

	if len(args) != 2 {
		return nil, errors.New("<name> <value>")
	}

	name := args[0]
	value, err := strconv.Atoi(args[1])

	if err != nil {
		return nil, errors.New("invalid number")
	}

	fmt.Printf("*** name = %s, value = %d \n", name, value)

	err = stub.PutState(name, []byte(strconv.Itoa(value)))

	return nil, err
}

func (t *SampleChaincode) Query(stub *shim.ChaincodeStub, function string, args []string) ([]byte, error) {

	if len(args) != 1 {
		return nil, errors.New("<name>")
	}

	return stub.GetState(args[0])
}

func main() {
	err := shim.Start(new(SampleChaincode))

	if err != nil {
		fmt.Printf("Error starting Sample chaincode: %s \n", err)
	}
}
