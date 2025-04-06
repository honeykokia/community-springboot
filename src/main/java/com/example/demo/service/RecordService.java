package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.bean.RecordBean;
import com.example.demo.bean.UserBean;
import com.example.demo.dto.RecordRequest;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.RecordRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.response.SuccessResponse;
import com.example.demo.utils.AuthUtil;

@Service
public class RecordService {

    @Autowired
    private RecordRepository recordRepo;

    @Autowired
    private UserRepository userRepo;

    public ResponseEntity<?> recordGet() {

        Long userId = AuthUtil.getCurrentUserId();
        Optional<List<RecordBean>> recordOpt = recordRepo.findByUserId(userId);
        
        SuccessResponse response;
        if (recordOpt.isEmpty()) {
            response = new SuccessResponse(new RecordBean());
        }else{
            response = new SuccessResponse(recordOpt.get());
        }


        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> recordSave(RecordRequest request){
        Long userId = AuthUtil.getCurrentUserId();
        UserBean user = userRepo.findById(userId).get();
        RecordBean record = new RecordBean();
        record.setUser(user);
        record.setItem_title(request.getItem_title());
        record.setItem_description(request.getItem_description());
        record.setItem_price(request.getItem_price());
        record.setItem_type(request.getItem_type());
        record.setItem_image(request.getItem_image());
        record.setItem_date(request.getItem_date());
        record.setCreated_at(LocalDateTime.now());
        recordRepo.save(record);

        return ResponseEntity.ok(new SuccessResponse(null));
    }

    public ResponseEntity<?> recordDelete(long recordId){

        Optional<RecordBean> accountOpt = recordRepo.findById(recordId);
        if(accountOpt.isEmpty()){
            throw new ApiException(Map.of("general","刪除失敗"));
        }
        RecordBean record = accountOpt.get();
        recordRepo.delete(record);
        return ResponseEntity.ok(new SuccessResponse(null));
    }
    
}
