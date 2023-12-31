'use client'
import axios from "axios"
import { useState, useEffect } from "react"
import '@/styles/MainPageButton.css'
import Footer from "@/app/Footer"
import ReceiptCircle from "@/components/WriteReceipt/ReceiptCircle"
import { useRouter } from "next/navigation"

const Retry = () => {
    const router = useRouter();

    // 목표 기간 재입력
    const [inputValue, setInputValue] = useState<number>();

    // 챌린지 기본 정보
    const [productName, setProductName] = useState<string>("");
    const [productPrice, setProductPrice] = useState<number>();

    // 토큰 가져오기
    let token: string = "";
    
    useEffect(() => {
        if (typeof window !== "undefined") {
        token = window.localStorage.getItem("access_token");
        }
    }, []);

    // 챌린지 시작 api 호출
    const startNewChallenge = async () => {
        const requestBody = {
            challengePeriod: inputValue
        }
        if (typeof window !== "undefined") {
            token = window.localStorage.getItem("access_token");
            }
        await axios
            .post('/api/challenges/retry', requestBody, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": `application/json`
                },
            })
            .then((response) => {
                console.log('챌린지 다시 시작 성공', response)
                // 챌린지 다시 시작 제출 성공 하면 메인 페이지로 보내기
                router.push('/main')
            })
            .catch((error) => {
                console.log('챌린지 다시 시작 실패', error)
            })
    }

    // 실패한 챌린지 기본 정보 가져오는 api 호출
    const CheckChallengeInfo = async () => {
        await axios
            .get('/api/challenges/in-progress', {
                headers: {
                    Authorization: `Bearer ${token}`,
                  },
            })
            .then((response) => {
                console.log('챌린지 기본 정보 조회 성공', response.data)
                setProductName(response.data.data.productName)
                setProductPrice(response.data.data.productPrice)
            })
            .catch((error) => {
                console.log('챌린지 기본 정보 조회 실패', error)
            })
    }

    useEffect(() => {
        CheckChallengeInfo()
    }, [])
  
    return (
        <div className="flex flex-col items-center mt-[10vh]">
            <div className="z-10 -mb-[15px]">
                <ReceiptCircle />
            </div>
            <div className="w-[70vw] h-[60vh] min-w-[320px] max-w-[4px] rounded-md bg-[#EEE] flex flex-col items-center">
                <div className="w-[40%] rounded-xl bg-[#D9D9D9] text-[25px] flex justify-center items-center tracking-widest font-semibold mt-[30px]">AKGIMI</div>
                <div className="w-[70%] h-[7%] ps-[10px] rounded-lg bg-[#FFF] flex items-center text-[#757575] tracking-wide ps-[5px] mt-[4vh]">{productName}</div>
                <div className="w-[70%] h-[7%] ps-[10px] rounded-lg bg-[#FFF] flex items-center text-[#757575] tracking-wide ps-[5px] mt-[1vh]">{productPrice}</div>
                <div className="w-[70%] mt-6 text-[#757575]">목표기간을 다시 정해주세요</div>
                <div className="flex z-10 justify-center gap-5">
                    <div className="rounded-full w-8 h-8 bg-[#EEE]"></div>
                    <div className="rounded-full w-8 h-8 bg-[#EEE]"></div>
                    <div className="rounded-full w-8 h-8 bg-[#EEE]"></div>
                    <div className="rounded-full w-8 h-8 bg-[#EEE]"></div>
                </div>
                {/* 스크롤바가 안 숨겨짐 */}
                <div className="w-[70%] h-[15vh] bg-white rounded-lg -mt-[15px] flex items-center">
                    <button className="ms-3 w-[50px] h-[50px] px-3 focus:bg-[#EEE] rounded-full" onClick={() => setInputValue(10)}>10</button>
                    <button className="w-[50px] h-[50px] focus:bg-[#EEE] rounded-full" onClick={() => setInputValue(20)}>20</button>
                    <button className="w-[50px] h-[50px] focus:bg-[#EEE] rounded-full" onClick={() => setInputValue(30)}>30</button>
                    <button className="w-[50px] h-[50px] focus:bg-[#EEE] rounded-full" onClick={() => setInputValue(40)}>40</button>
                </div>
            </div>
          
            <button className="button-common-small blue-btn absolute bottom-[15vh] max-w-[300px]"
                onClick={startNewChallenge}
            >챌린지 시작</button>
        <Footer />
        </div>
    )
}

export default Retry