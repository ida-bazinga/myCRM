package com.haulmont.thesis.crm.core.app.service;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.core.global.*;
import com.haulmont.thesis.core.entity.ContactPerson;
import com.haulmont.thesis.crm.core.app.worker.ContactPersonWorker;
import com.haulmont.thesis.crm.core.config.CrmConfig;
import com.haulmont.thesis.crm.entity.*;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;

@Service(CampaignTargetService.NAME)
public class CampaignTargetServiceBean implements CampaignTargetService {

    @Inject
    protected Metadata metadata;
    @Inject
    protected ContactPersonWorker contactWorker;
    @Inject
    protected Configuration configuration;
    @Inject
    protected DataManager dataManager;

    @SuppressWarnings("unchecked")
    public List<CampaignTarget> getTargets(Map<String, Object> params) {

        List<ExtCompany> selectCompany = new ArrayList<>();
        selectCompany.addAll(((Set) params.get("companies")));

        CommunicationTypeEnum type = (CommunicationTypeEnum) params.get("type");
        ContactRole role = (ContactRole) params.get("role");
        // TODO: 22.03.2018 for add communication from contact person frame
        //Set<ExtContactPerson> persons = (Set<ExtContactPerson>) params.get("persons");

        if (selectCompany == null || selectCompany.isEmpty() || type == null) return Collections.emptyList();

        List<List<ExtCompany>> chunksCompany = chunks(selectCompany, 1999);

        List<ExtCompany> companies = new ArrayList<>();

        for (List<ExtCompany> chunkCompany:chunksCompany) {
            companies.addAll(reloadCompanies(chunkCompany));
        }

        List<CampaignTarget> result = new LinkedList<>();

        for (ExtCompany company : companies) {
            if (company.getContactPersons() != null) {
                Map<Communication, ExtContactPerson> phones = getPhones(company, type, role);
                if (!phones.isEmpty()) {
                    int counter = 0;
                    for (Map.Entry<Communication, ExtContactPerson> entry : phones.entrySet()) {
                        counter++;
                        Iterable<CampaignTarget> ft = filterEqualityCompanyComm(result, entry.getKey());
                        result.add(createTarget(company, entry.getValue(), entry.getKey(), counter,
                                ft != null && ft.iterator().hasNext() ? "double" : "targetOk"));
                    }
                } else {
                    result.add(createTarget(company, "noCommunications"));
                }
            } else {
                result.add(createTarget(company, "noContacts"));
            }
        }
        return result;
    }

    protected Map<Communication, ExtContactPerson> getPhones(ExtCompany company, CommunicationTypeEnum type, ContactRole role) {
        if (company == null) return Collections.emptyMap();
        CrmConfig config = configuration.getConfig(CrmConfig.class);
        int maxCountCommunications = type.compareTo(CommunicationTypeEnum.email) != 0 ? config.getMaxCountCommunicationsForCampaign() : config.getMaxCountCommunicationsForEmailCampaign();
        int minMainPartLength = type.compareTo(CommunicationTypeEnum.email) != 0 ? config.getMinMainPartLengthForCampaign() : 1;

        Map<Communication, ExtContactPerson> phones = new LinkedHashMap<>();
        Map<ExtContactPerson, Set<Communication>> contactsPhones = getContactsPhones(company, maxCountCommunications, type, role, minMainPartLength);
        Collection<ExtContactPerson> contacts = contactsPhones.keySet();

        for(int c = 1; c < maxCountCommunications+1 && phones.size() < maxCountCommunications+1; c++){
            Iterator<ExtContactPerson> contactIterator = contacts.iterator();
            for(int p = 1; p < maxCountCommunications+1 && contactIterator.hasNext(); p++){
                ExtContactPerson contact = contactIterator.next();
                Communication comm = getContactPhone(contactsPhones.get(contact), c);
                if (comm != null && !phones.containsKey(comm)){
                    phones.put(comm, contact);
                }
            }
        }
        return phones;
    }

    protected Map<ExtContactPerson, Set<Communication>> getContactsPhones(ExtCompany company, int maxCountCommunications,
                                                                          CommunicationTypeEnum type, ContactRole role, int length){
        if (company == null) return Collections.emptyMap();
        int commCount = 0;
        Map<ExtContactPerson, Set<Communication>> result = new LinkedHashMap<>();
        for (ContactPerson contact : company.getContactPersons()){
            ExtContactPerson cp = (ExtContactPerson) contact;
            if (role != null && cp.getContactRoles() != null && !cp.getContactRoles().contains(role)){
                continue;
            }
            Set<Communication> communications = getTreeSet();
            communications.addAll(contactWorker.getPrefCommunications(cp, type, length));
            if(communications.size() > 0) {
                commCount++;
                result.put(cp, communications);
            }
            if (commCount >= maxCountCommunications) break;
        }
        return result;
    }

    @SuppressWarnings("Duplicates")
    protected TreeSet<Communication> getTreeSet(){
        return new TreeSet<>(new Comparator<Communication>() {
            public int compare(Communication o1, Communication o2) {
                //Поднимаем наверх объекты с мельшим приоритетом
                if (o1.getPref() == null) return 1;
                if (o2.getPref() == null) return -1;
                int sort1 = o1.getPref().compareTo(o2.getPref());
                if (sort1 == 0){
                    if (o1.getPriority() == null) return 1;
                    if (o2.getPriority() == null) return -1;
                    return o1.getPriority().compareTo(o2.getPriority());
                }
                return sort1;
            }
        });
    }

    @Nullable
    protected Communication getContactPhone(Set<Communication> communications, int seq) {
        Iterator<Communication> iterator = communications.iterator();
        for (int i = 1; i <= seq && iterator.hasNext(); i++){
            Communication result = iterator.next();
            if (i == seq) return result;
        }
        return null;
    }

    protected CampaignTarget createTarget(ExtCompany company, String descrMsgKey){
        return createTarget(company, null, null, -1, descrMsgKey);
    }

    protected CampaignTarget createTarget(ExtCompany company, ExtContactPerson contact, Communication communication, int priority, String descrMsgKey){
        CampaignTarget target = metadata.create(CampaignTarget.class);
        target.setCompany(company);
        target.setContactPerson(contact);
        target.setCommunication(communication);
        target.setPriority(priority);
        target.setDescription(descrMsgKey);

        return target;
    }

    protected Iterable<CampaignTarget> filterEqualityCompanyComm(Collection<CampaignTarget> targets, final Communication item) {
        return Iterables.filter(targets, new Predicate<CampaignTarget>() {
            @Override
            public boolean apply(CampaignTarget input) {
                Preconditions.checkNotNullArgument(item, "Communication is not specified");
                return input.getCommunication() != null && item.getMainPart().equals(input.getCommunication().getMainPart());
            }
        });
    }

    protected List<ExtCompany> reloadCompanies(List<ExtCompany> companies){
        LoadContext ctx = new LoadContext(ExtCompany.class);
        View contactView = metadata.getViewRepository().getView(ExtContactPerson.class, "_local-crm");
        View companyView = metadata.getViewRepository()
                .getView(ExtCompany.class, "browse")
                .addProperty("contactPersons", contactView, true);
        ctx.setView(companyView);
        ctx.setQueryString("select e from df$Company e where e.id in :ids")
                .setParameter("ids", companies);
        return dataManager.loadList(ctx);
    }

    static <T> List<List<T>> chunks(List<T> list, final int L) {
        List<List<T>> parts = new ArrayList<List<T>>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<T>(list.subList(i, Math.min(N, i + L))));

        }
        return parts;
    }
}
