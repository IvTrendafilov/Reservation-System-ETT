package group18.eet.reservationsystem.settings.services;

import group18.eet.reservationsystem.settings.entities.Settings;
import group18.eet.reservationsystem.settings.repositories.SettingsRepository;
import group18.eet.reservationsystem.utils.BaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
public class SettingsService extends BaseService<Settings, SettingsRepository> {

    private final SettingsRepository settingsRepository;

    @Autowired
    public SettingsService(SettingsRepository settingsRepository) {
        super(settingsRepository);
        this.settingsRepository = settingsRepository;
    }

    public Settings edit(Settings settings) {
        if (settings.getMaxBookingTimeLength() < 0 || settings.getMaxDevicesPerReservation() < 0) {
            throw new RuntimeException("There is a input with value lower than 0");
        }

        if (settings.getLoungeSchedule() == null || !settings.getLoungeSchedule().checkScheduleValidity()) {
            throw new RuntimeException("Either the lounge schedule is empty or it is invalid");
        }

        return settingsRepository.save(settings);
    }

    public Settings findSettings() {
        return settingsRepository.findAll().get(0);
    }
}
