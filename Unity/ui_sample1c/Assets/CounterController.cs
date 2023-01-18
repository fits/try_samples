using UnityEngine;
using TMPro;

public class CounterController : MonoBehaviour
{
    [SerializeReference]
    private TextMeshProUGUI counter;

    public void OnClick()
    {
        Debug.Log("OnClick Button");

        var v = int.Parse(counter.text) + 1;

        counter.text = v.ToString();
    }
}
