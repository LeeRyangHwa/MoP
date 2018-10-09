package cosmoslab.example.com.ClubMultiTracking.Decorator;

import android.util.Log;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Calendar;

import static android.graphics.Color.RED;

/**
 * Decorate several days with a dot
 */
public class EventDecorator implements DayViewDecorator {

    private final Calendar calendar = Calendar.getInstance();

    int YY = calendar.get(Calendar.YEAR);
    int YDay = calendar.get(Calendar.MONTH);
    int Day = calendar.get(Calendar.DAY_OF_MONTH);

    public String x, y;

    public EventDecorator(String s) {
        x = s;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {

        day.copyTo(calendar);
        YY = calendar.get(Calendar.YEAR);
        YDay = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        y = String.valueOf(YY) + String.valueOf(YDay) + String.valueOf(Day);

        Log.i("날짜  값!!!***********", x);

        return y.equals(x);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new DotSpan(5, RED));
    }
}