using UnityEngine;

public class CounterController : MonoBehaviour
{
    private GameObject counter;

    void Start()
    {
        counter = GameObject.Find("Counter");
    }

    public void OnClick()
    {
        Debug.Log("OnClick Button");
        counter.SendMessage("CountUp");
    }
}
