using UnityEngine;
using UnityEngine.EventSystems;

public class CounterController : MonoBehaviour
{
    [SerializeReference]
    private GameObject target;

    public void OnClick()
    {
        Debug.Log("OnClick Button");

        ExecuteEvents.Execute<ICounter>(target, null, (x, _) => x.CountUp());
    }
}
