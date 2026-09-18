package com.company.user.api.interfaces;


import com.company.user.api.response.WalletRecordResp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface WalletRecordApi {

	@GetMapping("/pageMain")
	List<WalletRecordResp> pageMain(@RequestParam("current") Integer current, @RequestParam("size") Integer size);
}