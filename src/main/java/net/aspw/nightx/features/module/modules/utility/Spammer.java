package net.aspw.nightx.features.module.modules.utility;

import net.aspw.nightx.event.EventTarget;
import net.aspw.nightx.event.UpdateEvent;
import net.aspw.nightx.features.module.Module;
import net.aspw.nightx.features.module.ModuleCategory;
import net.aspw.nightx.features.module.ModuleInfo;
import net.aspw.nightx.utils.misc.RandomUtils;
import net.aspw.nightx.utils.timer.MSTimer;
import net.aspw.nightx.utils.timer.TimeUtils;
import net.aspw.nightx.value.BoolValue;
import net.aspw.nightx.value.IntegerValue;
import net.aspw.nightx.value.TextValue;

import java.util.Random;

@ModuleInfo(name = "Spammer", category = ModuleCategory.UTILITY)
public class Spammer extends Module {

    private final TextValue messageValue = new TextValue("Message", "D.o.w.n.l.o.a.d. .N.i.g.h.t.X. .C.l.i.e.n.t.");
    private final BoolValue customValue = new BoolValue("Custom", false);    private final IntegerValue maxDelayValue = new IntegerValue("MaxDelay", 1500, 0, 5000, "ms") {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            final int minDelayValueObject = minDelayValue.get();

            if (minDelayValueObject > newValue)
                set(minDelayValueObject);
            delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get());
        }
    };
    private final TextValue blankText = new TextValue("Placeholder guide", "", () -> customValue.get());
    private final TextValue guideFloat = new TextValue("%f", "Random float", () -> customValue.get());    private final IntegerValue minDelayValue = new IntegerValue("MinDelay", 1500, 0, 5000, "ms") {

        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            final int maxDelayValueObject = maxDelayValue.get();

            if (maxDelayValueObject < newValue)
                set(maxDelayValueObject);
            delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get());
        }
    };
    private final TextValue guideInt = new TextValue("%i", "Random integer (max length 10000)", () -> customValue.get());
    private final TextValue guideString = new TextValue("%s", "Random string (max length 9)", () -> customValue.get());
    private final TextValue guideShortString = new TextValue("%ss", "Random short string (max length 5)", () -> customValue.get());
    private final TextValue guideLongString = new TextValue("%ls", "Random long string (max length 16)", () -> customValue.get());
    private final MSTimer msTimer = new MSTimer();

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if (msTimer.hasTimePassed(delay)) {
            mc.thePlayer.sendChatMessage(customValue.get() ? replace(messageValue.get()) : messageValue.get() + " >" + RandomUtils.randomString(5 + new Random().nextInt(5)) + "<");
            msTimer.reset();
            delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get());
        }
    }

    private String replace(String object) {
        final Random r = new Random();

        while (object.contains("%f"))
            object = object.substring(0, object.indexOf("%f")) + r.nextFloat() + object.substring(object.indexOf("%f") + "%f".length());

        while (object.contains("%i"))
            object = object.substring(0, object.indexOf("%i")) + r.nextInt(10000) + object.substring(object.indexOf("%i") + "%i".length());

        while (object.contains("%s"))
            object = object.substring(0, object.indexOf("%s")) + RandomUtils.randomString(r.nextInt(8) + 1) + object.substring(object.indexOf("%s") + "%s".length());

        while (object.contains("%ss"))
            object = object.substring(0, object.indexOf("%ss")) + RandomUtils.randomString(r.nextInt(4) + 1) + object.substring(object.indexOf("%ss") + "%ss".length());

        while (object.contains("%ls"))
            object = object.substring(0, object.indexOf("%ls")) + RandomUtils.randomString(r.nextInt(15) + 1) + object.substring(object.indexOf("%ls") + "%ls".length());
        return object;
    }





    private long delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get());


}
