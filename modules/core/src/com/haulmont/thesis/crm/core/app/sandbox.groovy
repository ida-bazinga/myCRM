import com.haulmont.cuba.core.entity.Entity
import com.haulmont.cuba.core.global.*
import com.haulmont.thesis.core.entity.TsCardRole
import com.haulmont.thesis.crm.entity.ExtEmployee
import com.haulmont.thesis.crm.entity.InvDoc
import com.haulmont.workflow.core.entity.CardRole
import com.haulmont.workflow.core.global.TimeUnit

def dataManager = AppBeans.get(DataManager.class)
def metadata = AppBeans.get(Metadata.class)

def view = metadata.getViewRepository().getView(InvDoc.class, "edit")
card = dataManager.reload(card, view)

LoadContext loadContext = new LoadContext(ExtEmployee.class)
        .setView("browse");
loadContext.setQueryString("select emp from crm\$InvDocBugetDetail bug join bug.department.employees emp where bug.invDoc.id = :card and emp.position.isDeptChief = '1'")
        .setParameter("card", card.id)
List<ExtEmployee> employees_1 = dataManager.loadList(loadContext)

loadContext.setQueryString("select team.employee from crm\$InvDocBugetDetail bug join bug.project.teams team where bug.invDoc.id = :card and team.employee.position.isDeptChief = '1' and team.roleInProject.code = '200'")
        .setParameter("card", card.id)
List<ExtEmployee> employees = dataManager.loadList(loadContext)

//employees = employees.addAll(employeesFromProject)

def proc = card.procs.find{it.proc.code == "ExpenseBill"}
def procRole =  proc.proc.roles.find{it.code == "CFO"}

loadContext = new LoadContext(CardRole.class)
    .setView("card-edit")
loadContext.setQueryString("select cr from wf\$CardRole cr where cr.card.id = :id and cr.procRole.id = :pid")
    .setParameter("id", card.id)
    .setParameter("pid", procRole.id)

def toRemove = dataManager.loadList(loadContext)

Date currentDate = AppBeans.get(TimeSource.class).currentTimestamp()

def toCommit = new HashSet<Entity>()

employees.each { emp ->
    if (emp.dismissalDate >= currentDate || emp.dismissalDate == null) {
        def user = emp.getUser()
        TsCardRole cfo = metadata.create(TsCardRole.class)

        cfo.setUser(user)
        cfo.setReadonly(false)
        cfo.setProcRole(procRole)
        cfo.setDuration(3)
        cfo.setTimeUnit(TimeUnit.DAY)
        cfo.setNotifyByEmail(true)
        cfo.setNotifyByCardInfo(true)
        cfo.setCode("CFO")
        cfo.setSortOrder(1)
        cfo.setCard(card)

        toCommit.add(cfo)
    }
}
def context = new CommitContext(toCommit, toRemove)
dataManager.commit(context)

return true