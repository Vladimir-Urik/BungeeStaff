package bungeestaff.bungee.system.storage;

import bungeestaff.bungee.system.staff.StaffUser;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IStaffStorage extends IStorage {

    CompletableFuture<Boolean> save(StaffUser user);

    CompletableFuture<StaffUser> load(UUID uniqueID);

    CompletableFuture<Void> saveAll(Collection<StaffUser> users);

    CompletableFuture<Set<StaffUser>> loadAll();

    CompletableFuture<Boolean> delete(UUID uniqueID);
}
