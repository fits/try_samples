using UnityEngine;
using TMPro;

public class Counter : MonoBehaviour
{
    private int count = 0;
    private TextMeshProUGUI t;

    void Start()
    {
        t = GetComponent<TextMeshProUGUI>();
    }

    public void CountUp()
    {
        Debug.Log("called CountUp");

        count++;
        t.text = count.ToString();
    }
}
