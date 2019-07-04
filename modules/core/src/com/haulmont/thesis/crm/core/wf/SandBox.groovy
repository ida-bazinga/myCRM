import com.haulmont.cuba.core.global.AppBeans
import com.haulmont.cuba.core.global.DataManager
import com.haulmont.cuba.core.global.TimeSource

card.setEndTimeFact(AppBeans.get(TimeSource.class).currentTimestamp())
def dataManager = AppBeans.get(DataManager.class)
dataManager.commit(owner)

return true