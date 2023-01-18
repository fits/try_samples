using UnityEngine;
using UnityEngine.EventSystems;

using TMPro;

public interface ICounter : IEventSystemHandler
{
    void CountUp();
}

public class Counter : MonoBehaviour, ICounter
{
    [SerializeField]
    private int count = 0;

    [SerializeReference]
    private TextMeshProUGUI textUi;

    void Start()
    {
        updateCounter();
    }

    public void CountUp()
    {
        count++;
        updateCounter();
    }

    private void updateCounter()
    {
        textUi.text = count.ToString();
    }
}
