using UnityEngine;

public class CounterController : MonoBehaviour
{
    [SerializeReference]
    private Counter counter;

    public void OnClick()
    {
        Debug.Log("OnClick Button");
        counter.CountUp();
    }
}
